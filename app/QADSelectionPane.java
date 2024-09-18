package app;
import PrEis.utils.Pgfx;
import processing.core.PApplet;
import processing.core.PVector;

public class QADSelectionPane {

  /** <b>Not Applicable</b> i.e. null */
  private static final String NA = "-(N/A)-";

  private PVector pos, dim;
  private float xOff, xOff2, yOff;
  private AppUtils appUtils;
  private PApplet app;
  private String[] labels;


  public QADSelectionPane(AppUtils inAppUtils, PVector iPos, PVector iDim){
    appUtils = inAppUtils;
    app = appUtils.app;

    labels = new String[]{
      "Config Name",  
      "Config Label", 
      "GZDoom Path",  
      "iWAD Path",    
      "WAD Path(s)",  
      "DEH Path (A/A)",
      "Gameplay WAD",
    };

    setTransform(iPos,iDim);
  }

  public void setTransform(PVector in_pos, PVector in_dim){
    pos=in_pos;
    dim=in_dim;
  }

  public void render(){

    
    app.strokeWeight(2);
    Pgfx.strokenofill(app,0,0,128);
    app.rectMode(PApplet.CORNER);
    app.rect(pos.x,pos.y,dim.x,dim.y);

    app.push(); 
    app.translate(pos.x, pos.y);
    app.clip(0,0,dim.x,dim.y);

    xOff=8;
    xOff2 = dim.x-(xOff*2);
    yOff=32;
    app.fill(0);
    app.textAlign(PApplet.LEFT,PApplet.BASELINE);
    app.textSize(32);
    app.text("CURRENT SELECTION",xOff,yOff);

    yOff+=8;
    app.strokeWeight(2);
    app.line(xOff,yOff,xOff2,yOff);

    app.textSize(16);

  
    xOff  = 16;
    xOff2 = 160;
    yOff  = 64;
    for(int i=0; i<labels.length; i++){
      app.text(labels[i],xOff,yOff);
      app.text(dispVal(i),xOff2,yOff);
      yOff+=32;
    }

    txtlbl_FUL();

    app.noClip();
    app.pop();
  }


  private String dispVal(int i){
    if(appUtils.curCfig==null){i=-1;}
    switch (i) {
      case 0:  return appUtils.curCfig.value==null      ? NA : appUtils.curCfig.value;
      case 1:  return appUtils.curCfig.label==null      ? NA : appUtils.curCfig.label;
      case 2:  return appUtils.curCfig.fpath_gzd==null  ? NA : appUtils.curCfig.fpath_gzd;
      case 3:  return appUtils.curCfig.fpath_iwad==null ? NA : appUtils.curCfig.fpath_iwad;
      case 4:  return appUtils.curCfig.fpath_wad==null  ? NA : appUtils.curCfig.fpath_wad;
      case 5:  return appUtils.curCfig.fpath_deh==null  ? NA : appUtils.curCfig.fpath_deh;
      case 6:  return appUtils.curCfig.fpath_gwad==null ? NA : appUtils.curCfig.fpath_gwad;
      default: return NA; 
    }
  }

  private void txtlbl_FUL(){
    app.text("Full Launch Command:", xOff, yOff);
    if(appUtils.curCfig==null){return;}
    xOff=32;
    yOff+=16;
    app.fill(0,160,0);
    app.textSize(14);    
    app.text(appUtils.curCfig.toLaunchCommand(), xOff,yOff,dim.x-24,128);
  }
}