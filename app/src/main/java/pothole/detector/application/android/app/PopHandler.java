package pothole.detector.application.android.app;

import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.example.myfirst.R;

public class PopHandler implements PopupMenu.OnMenuItemClickListener {

    Context context;
    View v;
    DataBaseHelper dataBaseHelper;
    int id;
    boolean deleted;
    private CallBack callback;


    PopHandler(Context context, View v, int id, CallBack callback) {
        this.context = context;
        this.v = v;
        dataBaseHelper = new DataBaseHelper(context);
        this.id = id;
        deleted = false;
        this.callback = callback;
    }

    public void showPopup() {
        PopupMenu popup = new PopupMenu(context,v );
        popup.setOnMenuItemClickListener(this);
        popup.getMenuInflater().inflate(R.menu.popup_menu,popup.getMenu());
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.item1:

                dataBaseHelper.deleteData(id);
                if(callback != null) {
                callback.methodToCall(); }

                return true;

            default:
                return false;
        }
    }

}

