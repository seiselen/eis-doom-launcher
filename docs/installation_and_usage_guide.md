### IOU CONTENT !!! ###

# Installing And Configuring `EisDoomLauncher`

## IDE Used To Dev/Test/Build

### IDE

`EisDoomLauncher` and the `PrEis` Library are developed, tested, and built using the **VSCode** IDE (version `1.94` A/O `10/05/24`) in a **Windows `10`** OS environment. This tutorial content assumes the user to have the same; else have the ability to follow along WRT their IDE/OS.

### IDE Extensions

I have Microsoft's [Debugger for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-debug), [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack), and [Test Runner for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-test) extensions; as well as [Red Hat Language Support for Java](https://marketplace.visualstudio.com/items?itemName=redhat.java); for which I understand the aforementioned to be the 'standard' set of extensions for working with `Java` projects in `VSCode`.

## Processing, PrEis, and `settings.json` file

### Processing
* **Note:** You **don't** need to specify Processing in your `settings.json` referenced libraries; as the `PrEis` `JAR` you must build as a dependency for `EisDoomLauncher` already contains its `core.jar`. Ergo consider the following as a(nother) suggestion to get some familiarity therewith.
* Download the Processing Environment [Processing Homepage](https://processing.org/)
* **Note:** A/O `10/05/24`, I have and am utilizing Processing Version `4.3`.
* Run the Processing editor app and make a test sketch or two to make sure it works.
* **Suggestion:** I'd even play around with it standalone for a bit as to get some familiarity.
  * The website links to several example and tutorial *follow-along* sketches; and
  * I also suggest Dan Shiffman's **Coding Train** [YouTube Channel](https://www.youtube.com/@TheCodingTrain) as he has a ton of excellent tutorials and *follow-along* projects for Processing (and it's JS/Web counterpart [P5JS](https://p5js.org/)). I learned much about Processing (and Creative Coding) from Dan's videos!

### PrEis
* Download the `PrEis` library at the following [GitHub Repo Link](https://github.com/seiselen/preis).
* **DEFICIENCY NOTE:** IOU docu within the `PrEis` repo for how to set it up and build it on your machine.
* However we'll assume that you've built `PrEis.jar` within your local environment of the `PrEis` source.

### `.vscode/settings.json` File
* Create a directory named `.vscode` at your `EisDoomLauncher` project root directory.
* Create a file named `.vscode/settings.json`.
* **Note:** [VSCode Reference](https://code.visualstudio.com/docs/editor/workspaces#_workspace-settings) for the above two steps.
* There are two minimal properties you will want to set within `settings.json`:
  1) `java.project.referencedLibraries` as a `JSON` array of one `String`, whose value should be the absolute filepath of your copy of the `PrEis.jar` file; and
  2) `java.project.exportJar.targetPath` as a `String`, whose value should be `build` folder within your `EisDoomLauncher` project root directory. I happen to reference its absolute dirpath, but you should (?) be able to reference its local path within the project root.



## GZDoom, `WAD` Collection, and `build/source_paths.json` file


### IOU intro
### IOU guide
### IOU dir structure for this to work
### Most of this info is also imminently old and likely wrong... oh well lol
`"DIR_DOOM_RT"` : absolute path of parent directory of `GzDoom` version(s) and WAD collection.
`"DIR_GZDOOM"`  : absolute path of directory containing target `GZDoom` executable.
`"DIR_ALLWADS"` : name of directory containing `WAD` collection.
`"DIR_IWADS"`   : name of directory containing `iWAD` files.
`"EXE_GZDOOM"`  : filename of `GZDoom` executable.
`"FIL_BRIGHT"`  : filename of (custom) `BRIGHTMAPS` `wad/pk3` provider.
`"FIL_EISEQP"`  : filename of custom gameplay `wad/pk3`


# `WAD` Collection and `loadinfo.json` Files

## `loadinfo` File Syntax -&- Semantics



# Building And Running `EisDoomLauncher`

## Running `EisDoomLauncher` In Debug Mode

## Building `EisDoomLauncher` Executable `JAR`

## Running `EisDoomLauncher` Executable `JAR`