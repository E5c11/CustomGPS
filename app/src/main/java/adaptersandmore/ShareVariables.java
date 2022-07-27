package adaptersandmore;

import android.content.Context;
import android.content.SharedPreferences;

public class ShareVariables {

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;
    private ResourceProvider rp;

    public ShareVariables(Context context) {
        pref = context.getSharedPreferences("MyPref", 0);
        editor = pref.edit();
        rp = new ResourceProvider(context);
    }

    public boolean getActiveActivity() {return pref.getBoolean("active_activity", true);}
    public void setActiveActivity(boolean start) {editor.putBoolean("active_activity", start).commit();}

    public boolean getRunning() {return pref.getBoolean("running", true);}
    public void setRunning(boolean running) {editor.putBoolean("running", running).commit();}


}
