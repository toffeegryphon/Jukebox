package edu.illinois.cs465.jukebox;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HostCreationGeneralFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HostCreationGeneralFragment extends SavableFragment {

    private HostCreationViewModel viewModel;

    TextInputEditText editTextName, editTextTheme, editTextDate, editTextTime;
    private EditText editTextDesc;

    private Calendar dateTime;
    DatePickerDialog.OnDateSetListener dateListener;
    DatePickerDialog dateDialog;
    TimePickerDialog.OnTimeSetListener timeListener;
    TimePickerDialog timeDialog;

    public HostCreationGeneralFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HostCreationGeneralFragment.
     */
    public static HostCreationGeneralFragment newInstance() {
        HostCreationGeneralFragment fragment = new HostCreationGeneralFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dateTime = Calendar.getInstance();
        dateListener = (calendarView, year, month, dayOfMonth) -> {
            dateTime.set(Calendar.YEAR, year);
            dateTime.set(Calendar.MONTH, month);
            dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            viewModel.setDate(dateTime.getTimeInMillis());
        };

        dateDialog = new DatePickerDialog(
                requireActivity(),
                dateListener,
                dateTime.get(Calendar.YEAR),
                dateTime.get(Calendar.MONTH),
                dateTime.get(Calendar.DAY_OF_MONTH)
        );

        timeListener = (calendarView, hourOfDay, minute) -> {
            dateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateTime.set(Calendar.MINUTE, minute);
            viewModel.setDate(dateTime.getTimeInMillis());
        };

        timeDialog = new TimePickerDialog(
                requireActivity(),
                timeListener,
                dateTime.get(Calendar.HOUR_OF_DAY),
                dateTime.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(getActivity())
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_host_creation_general, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(HostCreationViewModel.class);

        editTextName = view.findViewById(R.id.edit_text_name);

        editTextTheme = view.findViewById(R.id.edit_text_theme);
        editTextDate = view.findViewById(R.id.edit_text_date);
        editTextTime = view.findViewById(R.id.edit_text_time);
        editTextDesc = view.findViewById(R.id.edit_text_desc);

        editTextDesc.setMaxLines(1);
        editTextDesc.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        editTextDesc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    editTextDesc.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                    editTextDesc.setMaxLines(100);
                } else {
                    editTextDesc.setMaxLines(1);
                    editTextDesc.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                }
            }
        });
        editTextDate.setOnClickListener(v -> dateDialog.show());

        editTextTime.setOnClickListener(v -> timeDialog.show());

        LiveData<Long> dateMillis = viewModel.getDate();
        final Observer<Long> dateObserver = millis -> {
            editTextDate.setText(new SimpleDateFormat("M/dd/yyyy").format(new Date(millis)));
            editTextTime.setText(new SimpleDateFormat("h:mm aa").format(new Date(millis)));
        };
        dateMillis.observe(getViewLifecycleOwner(), dateObserver);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        return view;
    }

    public void save() {
        // TODO Can probably be called in onDetach
        viewModel.setString(HostCreationViewModel.USERNAME, editTextName);
        viewModel.setString(HostCreationViewModel.THEME, editTextTheme);
        viewModel.setString(HostCreationViewModel.DESCRIPTION, editTextDesc);
    }

    private void bindStringObserver(TextView view, String key) {
        final Observer<String> observer = view::setText;
        LiveData<String> data = viewModel.getString(key, "");
        data.observe(this, observer);
    }

    public void bindViewModel() {
        bindStringObserver(editTextName, HostCreationViewModel.USERNAME);
        bindStringObserver(editTextTheme, HostCreationViewModel.THEME);
        bindStringObserver(editTextDesc, HostCreationViewModel.DESCRIPTION);
    }
}