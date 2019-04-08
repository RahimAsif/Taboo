package net.rahimasif.apps.Taboo;


import android.app.DialogFragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Toast;

public class SetTimeDialog extends DialogFragment
{
    private String timeLeft;

    public static SetTimeDialog newInstance(String timeLeft)
    {
        SetTimeDialog d = new SetTimeDialog();

        Bundle args = new Bundle();
        args.putString("timeLeft", timeLeft);
        d.setArguments(args);

        return d;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        timeLeft = getArguments().getString("timeLeft");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        // Display the alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.time_picker_dialog, null);
        builder.setView(v);

        // Set the values for the minute picker and second picker
        final TimePicker timePicker = (TimePicker) v.findViewById(R.id.timePicker);
        final NumberPicker minutePicker = (NumberPicker) timePicker.findViewById(R.id.minute);
        final NumberPicker secondPicker = (NumberPicker) timePicker.findViewById(R.id.seconds);
        String [] s = timeLeft.split(":");
        minutePicker.setValue(Integer.parseInt(s[0]));
        secondPicker.setValue(Integer.parseInt(s[1]));


        builder.setTitle("Time Remaining");
        builder.setPositiveButton("OK", new Dialog.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                ((MainActivity)getActivity()).doPositiveClick(String.format("%d:%02d", minutePicker.getValue(), secondPicker.getValue()));
            }
        });

        builder.setNegativeButton("Cancel", new Dialog.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Toast.makeText(getActivity(), "Cancel Button pressed", Toast.LENGTH_SHORT).show();
            }
        });


        AlertDialog dialog = builder.create();

        return dialog;
    }
}
