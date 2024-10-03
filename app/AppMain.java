package app;
import PrEis.utils.FileSysUtils;
import PrEis.utils.JAResourceUtil;
import processing.core.PApplet;
import processing.event.MouseEvent;

import java.io.File;

public class AppMain extends PApplet {

  public static int CANVAS_WIDE = 1280;
  public static int CANVAS_TALL = 768;
  public static int CANVAS_WIDH = CANVAS_WIDE/2;
  public static int CANVAS_TALH = CANVAS_TALL/2;

  public static String assetPath;  
  private AppUtils autils;
  private AppGUI agui;

  public static void main(String[] args) {
    PApplet.main("app.AppMain"); 
  }

  public void settings(){
    this.size(CANVAS_WIDE, CANVAS_TALL); 
  }
  
  public void setup(){
    initAssetPath();
    JAResourceUtil.app = this;
    autils = new AppUtils(this);
    agui = new AppGUI(autils);
  }

  public void draw(){
    //-[UPDATE CALLS]----------#
    agui.update();
    //-[RENDER CALLS]----------#
    autils.render();
    agui.render();
  }

  public void mouseWheel(MouseEvent e){
    agui.onMouseWheel(e.getCount());
  }

  public void mousePressed(){
    agui.onMousePressed();
  }

  public void keyPressed(){
    if(key=='q' || key=='Q' || keyCode==PApplet.ESC){exit(); return;}
  }

  public void initAssetPath(){
    File f;
    String ad = EResPath.ASSETDIR.get();
    String bd = EResPath.BUILDIR.get();

    //=[ 'PRODUCTION MODE' CASE (I.E. STANDALONE LAUNCHED BY USER) ]============
    f = new File(FileSysUtils.pathConcat(sketchPath(), ad));
    if (f.exists() && f.isDirectory()){AppMain.assetPath = f.getAbsolutePath();}
    
    //=[ 'DEVELOPMENT MODE' CASE (I.E. DEBUG LAUNCHED BY VSCODE) ]==============
    f = new File(FileSysUtils.pathConcat(sketchPath(), bd, ad));
    if (f.exists() && f.isDirectory()){AppMain.assetPath = f.getAbsolutePath();}
  }

  /** Returns path concat of {@link #assetPath} with input subpath therefrom. */
  public static String fullpathOf(EResPath sp){
    return FileSysUtils.pathConcat(AppMain.assetPath,sp.get());
  }

}