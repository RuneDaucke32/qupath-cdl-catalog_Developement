/**
 * QuPath CDL extension catalog generator.
 *
 * Run this script from QuPath's script editor. It writes the catalog JSON used
 * by QuPath's extension manager. By default, output is catalog.json in the
 * current working directory; set the catalog.output system property to use a
 * different path.
 *
 * For headless checks, set -Dcatalog.output=/path/to/catalog.json and pass
 * --check to compare the generated content without rewriting the file.
 *
 * @requires QuPath v0.7.0+
 */

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import qupath.ext.extensionmanager.core.model.CatalogModel
import qupath.ext.extensionmanager.core.model.ExtensionModel
import qupath.ext.extensionmanager.core.model.ReleaseModel
import qupath.ext.extensionmanager.core.model.VersionRangeModel

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


Path outputPath = Paths.get(
    System.getProperty("catalog.output", "catalog.json")
).toAbsolutePath().normalize()
def scriptArgs = binding.hasVariable("args")
    ? binding.getVariable("args") as String[]
    : new String[0]
def checkOnly = scriptArgs.contains("--check")

def extensionList = []

def shapexRelease = new ReleaseModel(
    "v0.1.0",
    new URI(
        "https://github.com/RuneDaucke32/ShapeX-Developement/releases/download/" +
        "v0.1.0/QuPath%20ShapeX%20extension%20v0.1.0.jar"
    ),
    null,
    null,
    null,
    new VersionRangeModel("v0.7.0", null, null)
)

extensionList.add(new ExtensionModel(
    "ShapeX",
    "Export QuPath annotations for automated laser microdissection.",
    "CDL",
    new URI("https://github.com/RuneDaucke32/ShapeX-Developement"),
    false,
    List.of(shapexRelease)
))

def catalog = new CatalogModel(
    "QuPath-CDL development catalog",
    "Development catalog for QuPath extensions maintained by the CDL team.",
    extensionList
)

def duplicateReleaseNames = extensionList.collectEntries { extension ->
    def names = extension.releases().collect { release -> release.name() }
    def duplicates = names.countBy { name -> name }
        .findAll { name, count -> count > 1 }
        .keySet()
    [(extension.name()): duplicates]
}.findAll { extensionName, duplicates -> !duplicates.isEmpty() }

if (!duplicateReleaseNames.isEmpty()) {
    throw new IllegalStateException(
        "Duplicate release names found: " + duplicateReleaseNames
    )
}

def gson = new GsonBuilder()
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
    .create()
def json = gson.toJson(catalog) + "\n"

def parsedCatalog = gson.fromJson(json, CatalogModel)
if (parsedCatalog != catalog) {
    throw new IllegalStateException("Generated catalog failed model round-trip validation")
}

if (checkOnly) {
    if (!Files.exists(outputPath)) {
        throw new IllegalStateException("Catalog does not exist: " + outputPath)
    }
    def existing = Files.readString(outputPath, StandardCharsets.UTF_8)
    if (existing != json) {
        throw new IllegalStateException(
            "catalog.json is out of date; run cdl-catalog-generator.groovy"
        )
    }
    println "catalog.json matches cdl-catalog-generator.groovy"
    return
}

Files.createDirectories(outputPath.parent)
Files.writeString(outputPath, json, StandardCharsets.UTF_8)
println "Catalog saved to " + outputPath
