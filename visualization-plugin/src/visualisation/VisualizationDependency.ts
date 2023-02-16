import { exec } from "child_process";
import { exists, existsSync, mkdir, readdirSync, rmdirSync, statSync, unlink, unlinkSync, writeFileSync } from "fs";
import { getLogger } from "log4js";
import { promisify } from "util";
import { Disposable, Uri } from "vscode";
import { IveUri } from "./IveUri";


/**
 * Sysml Visualization wrapper that handles interaction with the visualization jar.
 * Requires a sysml library to be loaded for any non trivial sysml model.
 */
export class VisualizationDependency implements Disposable {

  private libPath: Uri;

  private usesExternalLib: Boolean;

  /**
   * @param artifactName name of the visualization jar
   * @param version full version of the jar
   * @param binDir where the jar is located
   * @param libDir points to the folder where the shipped default library files should be unpacked
   * @param tempPath points to folder where visualization files can be safely generated
   * @param externalLibraryPath optional parameter in case the client wants to provide his own sysml library for visualization
   */
  public constructor(
    private readonly artifactName: string,
    private readonly version: string,
    private readonly binDir: Uri,
    private readonly libDir: Uri,
    // dot path
    private readonly tempPath: Uri,
    externalLibraryPath?: Uri) {
    if (externalLibraryPath) {
      this.libPath = externalLibraryPath;
      this.usesExternalLib = true;
    }
    else{
      this.libPath = IveUri.appendUri(this.libDir, "sysml.library");
      this.usesExternalLib = false;
    }
  }

  private getJarPath(): Uri {
    return IveUri.appendUri(this.binDir, this.artifactName + "-" + this.version + "-jar-with-dependencies.jar");
  }

  public usesExternalLibrary(): Boolean {
    return this.usesExternalLib;
  }

  /**
   * Unpacks the default library shipped with the jar into the class's {@link libDir}.
   */
  public async unpackDefaultLibrary() {
    const asyncExecute = promisify(exec);

    const command = ["java", "-jar", this.getJarPath().fsPath, "-e", this.libDir.fsPath].join(" ");

    let extractionResult = await asyncExecute(command);

    let stderr = extractionResult.stderr;

    if(stderr.length != 0) {
      getLogger("viz").warn(stderr);
    }

    return this;
  }

  /**
   * Creates a svg in the temporary directory.
   * @param modelPath The sysml model requiring visualization
   * @returns an Uri to the generated svg file.
   */
  public async visualize(modelPath: Uri): Promise<Uri | undefined> {
    const command = ["java", "-jar", this.getJarPath().fsPath, "-l", this.libPath.fsPath, modelPath.fsPath].join(" ");

    const asyncExecute = promisify(exec);
    const asyncExists = promisify(exists);
    const asyncMkdir = promisify(mkdir);
    const asyncUnlink = promisify(unlink);

    getLogger("viz").trace("Generating visualization");

    let result;
    try {
      result = await asyncExecute(command);
      getLogger("viz").trace(result.stderr);
    } catch (error) {
      getLogger("viz").error(error);
      return Promise.reject();
    }

    let svgData = result.stdout;

    let svgUri = IveUri.appendUri(this.tempPath, IveUri.getFilenameWithoutExtension(modelPath) + ".svg");

    let dirExists = await asyncExists(this.tempPath.fsPath);

    if (!dirExists) {
      await asyncMkdir(this.tempPath.fsPath);
    }

    let svgExists = await asyncExists(svgUri.fsPath);

    if (svgExists) {
      await asyncUnlink(svgUri.fsPath);
    }

    writeFileSync(svgUri.fsPath, svgData);

    return svgUri;
  }

  dispose() {
    //until we upgrade to node 14.14.0
    const removeDir = function(uri: Uri) {
      if (existsSync(uri.fsPath)) {
        const files = readdirSync(uri.fsPath);

        if (files.length > 0) {
          files.forEach(function(filename) {
            if (statSync(IveUri.appendUri(uri, filename).fsPath).isDirectory()) {
              removeDir(IveUri.appendUri(uri, filename));
            } else {
              unlinkSync(IveUri.appendUri(uri, filename).fsPath);
            }
          });
          rmdirSync(uri.fsPath);
        } else {
          rmdirSync(uri.fsPath);
        }
      }
    };

    // we clear the unpacked default library
    if (!this.usesExternalLibrary()) {
      removeDir(this.libPath);
    }
  }
}