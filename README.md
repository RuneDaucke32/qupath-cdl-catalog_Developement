# QuPath-CDL development catalog

This repository is the testing catalog for CDL-managed [QuPath extensions](https://qupath.readthedocs.io/en/0.6/docs/intro/extensions.html#managing-extensions-with-the-extension-manager).

The checked-in `catalog.json` provides ShapeX v0.1.0 from the [`ShapeX-Developement`](https://github.com/RuneDaucke32/ShapeX-Developement) testing repository for QuPath 0.7.0 or later.

## Repository layout

- `catalog.json` is the file read by QuPath.
- `cdl-catalog-generator.groovy` is the source of truth used to generate it.
- `.github/workflows/validate-catalog.yml` runs that same Groovy generator against QuPath's catalog model on every push and pull request.

No Python environment or Python tooling is required.

## Generate the catalog

The generator follows the current BIOP catalog approach and uses the catalog model bundled with QuPath 0.7 or later.

1. Open QuPath 0.7 or later.
2. Open **Automate → Script editor**.
3. Open `cdl-catalog-generator.groovy`.
4. If necessary, change the `catalog.output` fallback near the top of the script from `catalog.json` to the absolute path of this repository's `catalog.json`.
5. Run the script and confirm the printed output path.

The script constructs QuPath's typed catalog model, checks duplicate release names, round-trips the generated JSON through the same model, and writes the snake-case field names expected by the extension manager. It also supports `--check` for CI. The catalog may still contain releases compatible with QuPath 0.6; only the maintainer-side generator requires QuPath 0.7 or later.

## ShapeX test release

The catalog downloads this release asset:

```text
https://github.com/RuneDaucke32/ShapeX-Developement/releases/download/v0.1.0/QuPath%20ShapeX%20extension%20v0.1.0.jar
```

Before testing installation, the ShapeX repository must therefore have a published (not draft) `v0.1.0` release containing an asset named exactly:

```text
QuPath ShapeX extension v0.1.0.jar
```

Both testing repositories and the release asset must be readable by QuPath. Make them public unless the QuPath installation has another supported way to authenticate to GitHub.

## Publish and use the catalog

1. Push this project to [`qupath-extension-cdl-Development`](https://github.com/RuneDaucke32/qupath-extension-cdl-Development).
2. Confirm that `catalog.json` is at the repository root and that the validation workflow passes.
3. In QuPath, open **Extensions → Manage extensions → Manage extension catalogs**.
4. Add `https://github.com/RuneDaucke32/qupath-extension-cdl-Development`.

QuPath discovers `catalog.json` from the repository URL. Release `main_url` values must point directly to GitHub-hosted `.jar` or `.zip` assets; required, optional, and Javadoc dependency URLs may use supported GitHub or Maven URLs.

## References

- [QuPath extension manager documentation](https://qupath.readthedocs.io/en/0.6/docs/intro/extensions.html#managing-extensions-with-the-extension-manager)
- [Official QuPath catalog model and schema](https://github.com/qupath/extension-catalog-model)
- [Official QuPath catalog](https://github.com/qupath/qupath-catalog)
- [BIOP community catalog and Groovy generator](https://github.com/BIOP/qupath-biop-catalog)
