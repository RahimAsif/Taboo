package net.rahimasif.apps.Taboo;

import android.preference.DialogPreference;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by RahimAsif on 9/7/17.
 */
public class TimePickerPreference extends DialogPreference
{
    private int lastMinute = 1;
    private int lastSecond = 30;
    private TimePicker picker = null;

    public TimePickerPreference(Context ctxt, AttributeSet attrs)
    {
        super(ctxt, attrs);

        setPositiveButtonText("Set");
        setNegativeButtonText("Cancel");
    }

    @Override
    protected View onCreateDialogView()
    {
        picker = new TimePicker(getContext());

        return(picker);
    }

    @Override
    protected void onBindDialogView(View v)
    {
        super.onBindDialogView(v);

        picker.setCurrentMinute(lastMinute);
        picker.setCurrentSecond(lastSecond);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult)
    {
        super.onDialogClosed(positiveResult);

        if (positiveResult)
        {
            lastMinute = picker.getCurrentMinute();
            lastSecond = picker.getCurrentSeconds();

            String time=String.valueOf(lastMinute)+":"+String.valueOf(lastSecond);

            if (callChangeListener(time))
            {
                persistString(time);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index)
    {
        return(a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue)
    {
        String time=null;

        if (restoreValue)
        {
            if (defaultValue==null)
            {
                time = getPersistedString("01:30");
            }
            else
            {
                time = getPersistedString(defaultValue.toString());
            }
        }
        else
        {
            time = defaultValue.toString();
        }

        lastMinute = getMinute(time);
        lastSecond = getSecond(time);
    }

    public String getTime()
    {
        String time;
        time = String.format("%d:%02d", lastMinute, lastSecond);

        return time;
    }

    private static int getMinute(String time)
    {
        String[] pieces=time.split(":");

        return(Integer.parseInt(pieces[0]));
    }

    private static int getSecond(String time)
    {
        String[] pieces=time.split(":");

        return(Integer.parseInt(pieces[1]));
    }
}
