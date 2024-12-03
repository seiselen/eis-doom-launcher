# EisDoomLoader `loadinfo` File Syntax -&- Semantics

## `iwad`
* **Blurb:** Which basal `iwad` should be used?
* **Values/Options:**
	* **No Specification:** defaults to `doom2`; else
	* `{doom, doom2, plut, tnt}`: for resp. basal `iwad`s, a/a
## `deh`
* **Blurb:** Which `deh` file should be used?
* **Values/Options:**
	* **No Specification:** don't (either because it does not exist, else don't use one)
	* `true`: the `deh` filename matches that of the `wad`
	* `string`: the filename of the `deh` to use

## `bex`
* **Blurb:** Which `bex` file should be used?
* **Values/Options:**
	* **No Specification:** don't (either because it does not exist, else don't use one)
	* `true`: the `bex` filename matches that of the `wad`
	* `string`: the filename of the `bex` to use
## `wad`
* **Blurb:** Which `wad` or`pk3` file should be used?
* **Values/Options:**
	* **No Specification:** the app currently grabs the filename of the first `wad` else `pk3` it sees
	* `string`: the filename of the `wad|pk3` to use; which means...
		* [!] yes - you need to manually spec the filename; and
		* [!] yes - you need to sync if ever changing names 
	* `true|string[]`: multiple `wad|pk3`s and I want the app to list them all individually
		* [!] they MUST share the same basal `iwad` and/or/xor `deh`, A/A
		* `true` implies list every `wad|pk3` in the dir, `string[]` explicitly enumerates them 
  * `false` || empty array i.e. `[]`: do **NOT** realize anything in this dir as an option!
    * **usage:** for *'hiding'* mapsets i.e. of which I don't normally play ergo don't want in the selection list
    * **real-world example usage:** `EndDoom Mapping Contest 2024` `WAD`s
## `gwad`
* **Blurb:** Which gameplay `wad` or `pk3` should be used?
* **Values/Options:**
	* **No Specification:** the app currently uses the `EisEquip` version specified in app code xor extern `json` app data
	* `string`: fill filepath of gwad to use
	* `false` implies to use NONE; i.e. simply load in the `iwad`, `deh` (a/a), and `wad|pk3`} 
		- [i] this assertion is REQUIRED if desired <span style="font-size:0.75em;">(rhyme not intended)</span> because of the current no-spec handling (see above)
## `brights`
* **Blurb:** Use the GZDoom `brightmaps` `pk3`?
* **Values/Options:**
	* **No Specification:** don't
	* `true`: yes
## `flags`
* **Blurb:** Special flags for unique/otherwise cases; comma-separated if multiple
* **Values/Options:**
	* `"ALT_LEV_WAD"`: Specifies that the WAD has a special other WAD containing an alternate or modified version of some level in the primary WAD; s.t primary WAD must ALSO be loaded.
		* **Example:** As-Seen-In `180 Minutes Pour Vivre`
		* **Specification:** must list both in `"wad"` as `string[]`; s.t. primary WAD listed `1st`
		* **Outcome:** Both the original and the special level will have separate entries

