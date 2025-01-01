package app;

import PrEis.gui.IUpdateCallback;
import PrEis.gui.AppFont;
import PrEis.gui.LabelType;
import PrEis.gui.PosOri;
import PrEis.gui.UIContainer;
import PrEis.gui.UILabel;
import PrEis.gui.UIManager;
import PrEis.utils.BBox;
import PrEis.utils.StringUtils;
import processing.core.PApplet;

class UILauncherInfoPane extends UIContainer {
  AppUtils aUtil;

  public UILauncherInfoPane(PApplet iApp, BBox iBBox, AppUtils iUtil) {
    super(iApp, iBBox);
    aUtil = iUtil;
    initStyles();
    initWidgets();
  }

  public static UILauncherInfoPane create(AppUtils au, BBox bb){
    return new UILauncherInfoPane(au.app, bb, au);
  }

  public static UILauncherInfoPane create(AppUtils au, UIManager um, BBox bb){
    return new UILauncherInfoPane(au.app, bb, au).bindManager(um).castTo(UILauncherInfoPane.class);
  }


  private void initStyles(){
    setStyleProp("fill", Integer.class, app.color(0,128));
    setStyleProp("strk_enabled", Integer.class, app.color(224,240,255));   
  }

  private void initWidgets(){
    AppFont   AF = AppFont.TEXT;
    LabelType LT = LabelType.TP;

    CurConfigUpdate   curTar;
    CurConfigUpdate[] targets = new CurConfigUpdate[]{
      new CurConfigUpdate(aUtil, ConfigProp.CF_LBL,  "Config Name" ),
      new CurConfigUpdate(aUtil, ConfigProp.FP_GZD,  "GZDoom Path" ),
      new CurConfigUpdate(aUtil, ConfigProp.FP_IWAD, "iWAD Path"   ),
      new CurConfigUpdate(aUtil, ConfigProp.FP_WAD,  "WAD Path(s)" ),
      new CurConfigUpdate(aUtil, ConfigProp.FP_DEH,  "DEH/BEX Path"),
      new CurConfigUpdate(aUtil, ConfigProp.FP_GWAD, "Gameplay Mod")
    };

    int bordCol = app.color(255,0);
    int fillCol = app.color(255,0);

    int xD = 160;
    int xO = 16;
    int xS = xD+xO+24;
    int yD = 32;
    int yO = 8;
    int yS = yD;

    addChild(
      UILabel.create(app, new BBox(xO, yO, 768, 40), "CURRENT SELECTION", AF, LT, null)
      .setStyleProp("strk_transp", Integer.class, bordCol)
      .setStyleProp("txt_size", Integer.class, 32)
    );

    yO+=40;

    for(int i=0; i<targets.length; i++){
      curTar = targets[i];
      addChildren(
        UILabel.create(app, new BBox(xO, yO, xD, yD), curTar.tarLabl, AF, LT, null)
        .setStyleProp("txt_anchor", PosOri.class, PosOri.LFT)
        .setStyleProp("strk_transp", Integer.class, bordCol)
        .setStyleProp("fill_transp", Integer.class, fillCol)
        .setStyleProp("txt_size", Integer.class, 20)
        ,

        UILabel.create(app, new BBox(xS, yO, 584, yD), " ", AF, LT, curTar)
        .setStyleProp("txt_anchor", PosOri.class, PosOri.LFT)
        .setStyleProp("strk_transp", Integer.class, bordCol)
        .setStyleProp("fill_transp", Integer.class, fillCol)        
        .setStyleProp("txt_size", Integer.class, 16)
        .setStyleProp("fill_txt", Integer.class, app.color(0,255,255))
        .setFontTypeΘ(AppFont.MONO)
      );
      yO+=yS;
    }

    String pad = StringUtils.charTimesN('-',16);

    curTar = new CurConfigUpdate(aUtil, ConfigProp.CLI_CMD, pad+" FULL CLI LAUNCH COMMAND "+pad);

    addChild(
      UILabel.create(app, new BBox(xO, yO, 768, 24), curTar.tarLabl, AF, LT, null)
      .setStyleProp("txt_anchor", PosOri.class, PosOri.CTR)
      .setStyleProp("txt_size", Integer.class, 16)
      .setStyleProp("strk_transp", Integer.class, bordCol)
      .setStyleProp("fill_transp", Integer.class, fillCol)
    );

    addChild(
      UILabel.create(app, new BBox(xO, yO+28, 768, 120), " ", AF, LT, curTar)
      .setStyleProp("txt_anchor", PosOri.class, PosOri.TL)
      .setStyleProp("strk_transp", Integer.class, bordCol)
      .setStyleProp("fill_transp", Integer.class, fillCol)        
      .setStyleProp("txt_size", Integer.class, 14)
      .setStyleProp("fill_txt", Integer.class, app.color(0,255,255))
      .setStyleProp("text_wrap", Boolean.class, true)
      .setFontTypeΘ(AppFont.MONO)
      .castTo(UILabel.class)
    );


  }


  public void render(){
    app.imageMode(PApplet.CORNER);
    app.clip(bbox.minX(), bbox.minY(), bbox.dimX(), bbox.dimY());
    super.render();
    app.noClip();
    //> Required to handle effects of clip, analogous to Dropdown
    renderFG();
  }

  private void renderFG(){
    app.stroke(style.strk_enabled);
    app.noFill();
    renderRect();    
  }



}


class CurConfigUpdate implements IUpdateCallback {
  private ConfigProp tarProp;
  private AppUtils   appUtil;
  public  String     tarLabl;
  public CurConfigUpdate(AppUtils inAU, ConfigProp inCP, String inTL){appUtil=inAU; tarProp=inCP; tarLabl=inTL;}
  public String getTxt() {return appUtil.getCurConfigProp(tarProp);}
}