package ua.tripguide.tripguideua.Utils;

        import android.content.Context;
        import android.util.AttributeSet;

public class SelectAgainSpinner extends android.support.v7.widget.AppCompatSpinner {

    private int lastSelected = 0;
    private int selectedFix = 0;
    private int selectedDoubleFix = 0;

    public SelectAgainSpinner(Context context)
    { super(context); }

    public SelectAgainSpinner(Context context, AttributeSet attrs)
    { super(context, attrs); }

    public SelectAgainSpinner(Context context, AttributeSet attrs, int defStyle)
    { super(context, attrs, defStyle); }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(this.lastSelected == this.getSelectedItemPosition() && getOnItemSelectedListener() != null){
            getOnItemSelectedListener().onItemSelected(this, getSelectedView(), this.getSelectedItemPosition(), getSelectedItemId());
        }
        if (selectedDoubleFix == -1)
            lastSelected = this.getSelectedItemPosition();
        if (selectedFix == -1)
            selectedDoubleFix = -1;
        if(!changed)
            selectedFix = -1;
        if (this.getSelectedItemPosition() != 6){
            selectedDoubleFix = 0;
        }

        super.onLayout(changed, l, t, r, b);
    }
}

