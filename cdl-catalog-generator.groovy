import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import qupath.ext.extensionmanager.core.model.CatalogModel
import qupath.ext.extensionmanager.core.model.ExtensionModel
import qupath.ext.extensionmanager.core.model.ReleaseModel
import qupath.ext.extensionmanager.core.model.VersionRangeModel

import java.nio.file.Files
import java.nio.file.Paths

def release = new ReleaseModel(
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

def catalog = new CatalogModel(
    "QuPath-CDL development catalog",
    "Development catalog for QuPath extensions maintained by the CDL team.",
    List.of(new ExtensionModel(
        "ShapeX",
        "Export QuPath annotations for automated laser microdissection.",
        "CDL",
        new URI("https://github.com/RuneDaucke32/ShapeX-Developement"),
        false,
        List.of(release)
    ))
)

def json = new GsonBuilder()
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
    .create()
    .toJson(catalog) + "\n"
def outputPath = Paths.get(
    System.getProperty("catalog.output", "catalog.json")
).toAbsolutePath().normalize()
def checkOnly = binding.hasVariable("args") &&
    (binding.getVariable("args") as String[]).contains("--check")

if (checkOnly) {
    if (!Files.exists(outputPath) || Files.readString(outputPath) != json) {
        throw new IllegalStateException("catalog.json is out of date")
    }
} else {
    Files.createDirectories(outputPath.parent)
    Files.writeString(outputPath, json)
}
