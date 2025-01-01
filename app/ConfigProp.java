package app;

public enum ConfigProp {
  CF_NAM,
  CF_LBL,
  FP_GZD,
  FP_IWAD,
  FP_DEH,
  FP_WAD,
  FP_BRIT,
  FP_LITE,
  FP_GWAD,
  CLI_CMD;

  public static ConfigProp[] toArray(){
    return new ConfigProp[]{
      CF_NAM, CF_LBL, FP_GZD, FP_IWAD, FP_DEH, FP_WAD, FP_BRIT, FP_GWAD, CLI_CMD
    };
  }
}