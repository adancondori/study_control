package com.acc.study_control.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.acc.study_control.R;
import com.acc.study_control.activities.MainActivity;
import com.acc.study_control.models.Code;
import com.acc.study_control.models.Member;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MemberFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MemberFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MemberFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_CODE = "";
    private static final String ARG_CODE_ID = "";

    // TODO: Rename and change types of parameters
    private Code code;

    private OnFragmentInteractionListener mListener;

    private static final String TAG = "AddMember";


    private String FIREBASE_URL = "https://to-do-list-ykro.firebaseio.com/";
    private String FIREBASE_CHILD = "test";
    private FirebaseFirestore firestoreDB;
    private boolean isEdit;
    private String docId;
    public EditText member_birthday;


    public MemberFragment() {
        // Required empty public constructor
        firestoreDB = FirebaseFirestore.getInstance();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MemberFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MemberFragment newInstance() {
        MemberFragment fragment = new MemberFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
        code = Code.first(Code.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_member, container, false);
        initView(view);
        return view;
    }

    public void initView(View view) {
        Button button = (Button) view.findViewById(R.id.add_event);
        member_birthday = (EditText) view.findViewById(R.id.member_birthday);
        member_birthday.setOnClickListener(this);

        Member event = null;
        if (getArguments() != null && getArguments().containsKey("member")) {
            event = getArguments().getParcelable("member");
            ((TextView) view.findViewById(R.id.add_tv)).setText("Miembro de mi Grupo Pequeño");
        }
        if (event != null) {
            ((TextView) view.findViewById(R.id.member_name)).setText(event.name);
            ((TextView) view.findViewById(R.id.member_ap_paterno)).setText(event.ap_paterno);
            ((TextView) view.findViewById(R.id.member_ap_materno)).setText(event.ap_materno);
            ((TextView) view.findViewById(R.id.member_ci)).setText(event.ci);
            ((TextView) view.findViewById(R.id.member_cell_phone)).setText(event.cell_phone);
            ((TextView) view.findViewById(R.id.member_birthday)).setText(event.birthday);
            //((TextView) view.findViewById(R.id.event_end_time_a)).setText(event.getEndTime());

            button.setText("Edit Event");
            isEdit = true;
            docId = event.id;
        }

        firestoreDB = FirebaseFirestore.getInstance();


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).showSpinner(getActivity(), true, "Enviando datos...");
                if (!isEdit) {
                    addEvent();
                } else {
                    updateEvent();
                }

            }
        });
    }

    public void addEvent() {
        Member event = createEventObj();
        addDocumentToCollection(event);
    }

    public void updateEvent() {
        Member event = createEventObj();
        updateDocumentToCollection(event);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.member_birthday) {
            showDatePickerDialog(member_birthday);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void addDocumentToCollection(Member member) {
        firestoreDB.collection("districts")
                .document(code.district_id)
                .collection("churches")
                .document(code.church_id)
                .collection("small_group")
                .document(code.group_id)
                .collection("members")
                .add(member)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "Event document added - id: "
                                + documentReference.getId());
                        restUi();
                        ((MainActivity) getActivity()).showSpinner(getActivity(), false, "Enviando datos...");
                        ((MainActivity) getActivity()).changeFragment();
                        //Toast.makeText(getActivity(),
                        //        "Event document has been added",
                        //        Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        ((MainActivity) getActivity()).showSpinner(getActivity(), false, "Enviando datos...");
                        Log.w(TAG, "Error adding event document", e);
                        Toast.makeText(getActivity(),
                                "Error al enviar Datos",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateDocumentToCollection(Member member) {
        firestoreDB.collection("districts")
                .document(code.district_id)
                .collection("churches")
                .document(code.church_id)
                .collection("small_group")
                .document(code.group_id)
                .collection("members")
                .document(docId)
                .set(member, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Event document updated ");
                        Toast.makeText(getActivity(),
                                "Event document has been updated",
                                Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding event document", e);
                        Toast.makeText(getActivity(),
                                "Event document could not be added",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void restUi() {
        ((TextView) getActivity()
                .findViewById(R.id.member_name)).setText("");
        ((TextView) getActivity()
                .findViewById(R.id.member_ap_paterno)).setText("");
        ((TextView) getActivity()
                .findViewById(R.id.member_ap_materno)).setText("");
        ((TextView) getActivity()
                .findViewById(R.id.member_ci)).setText("");
        ((TextView) getActivity()
                .findViewById(R.id.member_cell_phone)).setText("");
        ((TextView) getActivity()
                .findViewById(R.id.member_birthday)).setText("");
    }

    private Member createEventObj() {
        final Member event = new Member();
        event.name = ((TextView) getActivity()
                .findViewById(R.id.member_name)).getText().toString();
        event.ap_paterno = ((TextView) getActivity()
                .findViewById(R.id.member_ap_paterno)).getText().toString();
        event.ap_materno = ((TextView) getActivity()
                .findViewById(R.id.member_ap_materno)).getText().toString();
        event.ci = ((TextView) getActivity()
                .findViewById(R.id.member_ci)).getText().toString();
        event.cell_phone = ((TextView) getActivity()
                .findViewById(R.id.member_cell_phone)).getText().toString();
        event.birthday = ((TextView) getActivity()
                .findViewById(R.id.member_birthday)).getText().toString();

        return event;
    }


    private void showDatePickerDialog(final EditText editText) {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because january is zero
                final String selectedDate = twoDigits(day) + "/" + twoDigits(month + 1) + "/" + year;
                editText.setText(selectedDate);
            }
        });
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    private String twoDigits(int n) {
        return (n <= 9) ? ("0" + n) : String.valueOf(n);
    }
}
