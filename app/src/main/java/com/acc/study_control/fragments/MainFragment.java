package com.acc.study_control.fragments;

/**
 * Created by acondori on 11/05/2018.
 */

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.acc.study_control.R;
import com.acc.study_control.activities.MainActivity;
import com.acc.study_control.models.Code;
import com.acc.study_control.models.Group;
import com.acc.study_control.models.Member;
import com.acc.study_control.table.TableViewAdapter;
import com.acc.study_control.table.TableViewListener;
import com.acc.study_control.table.TableViewModel;
import com.evrencoskun.tableview.TableView;
import com.evrencoskun.tableview.adapter.AbstractTableAdapter;
import com.evrencoskun.tableview.filter.Filter;
import com.evrencoskun.tableview.pagination.Pagination;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements TableViewListener.MyCellClick {

    private EditText searchField;
    private Spinner moodFilter, genderFilter, itemsPerPage;
    public ImageButton previousButton, nextButton;
    public EditText pageNumberField;
    public TextView tablePaginationDetails;

    private AbstractTableAdapter mTableViewAdapter;
    private TableView mTableView;
    private Filter mTableFilter; // This is used for filtering the table.
    private Pagination mPagination; // This is used for paginating the table.


    // This is a sample class that provides the cell value objects and other configurations.
    private TableViewModel mTableViewModel;

    private boolean mPaginationEnabled = false;

    private static final String TAG = "ViewEventsFragment";
    private FirebaseFirestore firestoreDB;
    public List<Member> members;
    public Code code = null;
    public View layout = null;


    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_main, container, false);

        firestoreDB = FirebaseFirestore.getInstance();

        getDocumentsFromCollection();

        searchField = layout.findViewById(R.id.query_string);
        searchField.addTextChangedListener(mSearchTextWatcher);

        moodFilter = layout.findViewById(R.id.mood_spinner);
        moodFilter.setOnItemSelectedListener(mItemSelectionListener);

        genderFilter = layout.findViewById(R.id.gender_spinner);
        genderFilter.setOnItemSelectedListener(mItemSelectionListener);

        itemsPerPage = layout.findViewById(R.id.items_per_page_spinner);

        View tableTestContainer = layout.findViewById(R.id.table_test_container);

        previousButton = layout.findViewById(R.id.previous_button);
        nextButton = layout.findViewById(R.id.next_button);
        pageNumberField = layout.findViewById(R.id.page_number_text);
        tablePaginationDetails = layout.findViewById(R.id.table_details);

        if (mPaginationEnabled) {
            tableTestContainer.setVisibility(View.VISIBLE);
            itemsPerPage.setOnItemSelectedListener(onItemsPerPageSelectedListener);

            previousButton.setOnClickListener(mClickListener);
            nextButton.setOnClickListener(mClickListener);
            pageNumberField.addTextChangedListener(onPageTextChanged);
        } else {
            tableTestContainer.setVisibility(View.GONE);
        }

        // Let's get TableView
        mTableView = layout.findViewById(R.id.tableview);

        //initializeTableView();

        if (mPaginationEnabled) {
            mTableFilter = new Filter(mTableView); // Create an instance of a Filter and pass the
            // created TableView.

            // Create an instance for the TableView pagination and pass the created TableView.
            mPagination = new Pagination(mTableView);

            // Sets the pagination listener of the TableView pagination to handle
            // pagination actions. See onTableViewPageTurnedListener variable declaration below.
            mPagination.setOnTableViewPageTurnedListener(onTableViewPageTurnedListener);
        }
        initView();
        return layout;
    }

    public void initView() {
        FloatingActionButton actionButton = layout.findViewById(R.id.btn_add_member);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMember();
            }
        });

    }

    private void initializeTableView() {
        // Create TableView View model class  to group view models of TableView AbstractViewHolder
        mTableViewModel = new TableViewModel(getContext(), members);

        // Create TableView Adapter
        mTableViewAdapter = new TableViewAdapter(getContext(), mTableViewModel);

        mTableView.setAdapter(mTableViewAdapter);
        mTableView.setTableViewListener(new TableViewListener(mTableView, this));

        // Create an instance of a Filter and pass the TableView.
        //mTableFilter = new Filter(mTableView);

        // Load the dummy data to the TableView
        mTableViewAdapter.setAllItems(mTableViewModel.getColumnHeaderList(), mTableViewModel
                .getRowHeaderList(), mTableViewModel.getCellList());


        //mTableView.setHasFixedWidth(true);

        /*for (int i = 0; i < mTableViewModel.getCellList().size(); i++) {
            mTableView.setColumnWidth(i, 200);
        }*)
        //mTableView.setColumnWidth(0, -2);
        //mTableView.setColumnWidth(1, -2);
        /*mTableView.setColumnWidth(2, 200);
        mTableView.setColumnWidth(3, 300);
        mTableView.setColumnWidth(4, 400);
        mTableView.setColumnWidth(5, 500);*/

    }

    public void filterTable(String filter) {
        // Sets a filter to the table, this will filter ALL the columns.
        mTableFilter.set(filter);
    }

    public void filterTableForMood(String filter) {
        // Sets a filter to the table, this will only filter a specific column.
        // In the example data, this will filter the mood column.
        mTableFilter.set(TableViewModel.MOOD_COLUMN_INDEX, filter);
    }

    public void filterTableForGender(String filter) {
        // Sets a filter to the table, this will only filter a specific column.
        // In the example data, this will filter the gender column.
        mTableFilter.set(TableViewModel.GENDER_COLUMN_INDEX, filter);
    }

    // The following four methods below: nextTablePage(), previousTablePage(),
    // goToTablePage(int page) and setTableItemsPerPage(int itemsPerPage)
    // are for controlling the TableView pagination.
    public void nextTablePage() {
        mPagination.nextPage();
    }

    public void previousTablePage() {
        mPagination.previousPage();
    }

    public void goToTablePage(int page) {
        mPagination.goToPage(page);
    }

    public void setTableItemsPerPage(int itemsPerPage) {
        mPagination.setItemsPerPage(itemsPerPage);
    }

    // Handler for the changing of pages in the paginated TableView.
    private Pagination.OnTableViewPageTurnedListener onTableViewPageTurnedListener = new
            Pagination.OnTableViewPageTurnedListener() {
                @Override
                public void onPageTurned(int numItems, int itemsStart, int itemsEnd) {
                    int currentPage = mPagination.getCurrentPage();
                    int pageCount = mPagination.getPageCount();
                    previousButton.setVisibility(View.VISIBLE);
                    nextButton.setVisibility(View.VISIBLE);

                    if (currentPage == 1 && pageCount == 1) {
                        previousButton.setVisibility(View.INVISIBLE);
                        nextButton.setVisibility(View.INVISIBLE);
                    }

                    if (currentPage == 1) {
                        previousButton.setVisibility(View.INVISIBLE);
                    }

                    if (currentPage == pageCount) {
                        nextButton.setVisibility(View.INVISIBLE);
                    }

                    tablePaginationDetails.setText(getString(R.string.table_pagination_details, String
                            .valueOf(currentPage), String.valueOf(itemsStart), String.valueOf(itemsEnd)));

                }
            };


    private AdapterView.OnItemSelectedListener mItemSelectionListener = new AdapterView
            .OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            // 0. index is for empty item of spinner.
            if (position > 0) {

                String filter = Integer.toString(position);

                if (parent == moodFilter) {
                    filterTableForMood(filter);
                } else if (parent == genderFilter) {
                    filterTableForGender(filter);
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // Left empty intentionally.
        }
    };

    private TextWatcher mSearchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            filterTable(String.valueOf(s));
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    private AdapterView.OnItemSelectedListener onItemsPerPageSelectedListener = new AdapterView
            .OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            int itemsPerPage;
            switch (parent.getItemAtPosition(position).toString()) {
                case "All":
                    itemsPerPage = 0;
                    break;
                default:
                    itemsPerPage = Integer.valueOf(parent.getItemAtPosition(position).toString());
            }

            setTableItemsPerPage(itemsPerPage);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == previousButton) {
                previousTablePage();
            } else if (v == nextButton) {
                nextTablePage();
            }
        }
    };

    private TextWatcher onPageTextChanged = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int page;
            if (TextUtils.isEmpty(s)) {
                page = 1;
            } else {
                page = Integer.valueOf(String.valueOf(s));
            }

            goToTablePage(page);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void getDocumentsFromCollection() {
        String cad = "districts/" + code.district_id + "/churches/" + code.chruch_id + "/small_group/" + code.group_id + "/members";
        //db.collection("app").document("users").collection(uid).document("notifications")

        firestoreDB.collection("districts")
                .document(code.district_id)
                .collection("churches")
                .document(code.chruch_id)
                .collection("small_group")
                .document(code.group_id)
                .collection("members")
                //.whereEqualTo("type", eventType)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            members = new ArrayList<>();

                            for (DocumentSnapshot doc : task.getResult()) {
                                Member e = doc.toObject(Member.class);
                                e.id = doc.getId();
                                members.add(e);
                            }
                            initializeTableView();
                            Log.e(TAG, "entro a ON_COMPLETE MEMBER");
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        firestoreDB.collection("districts")
                .document(code.district_id)
                .collection("churches")
                .document(code.chruch_id)
                .collection("small_group")
                .document(code.group_id)
                .collection("members")
                //.whereEqualTo("type", eventType)
                .addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                            doc.getDocument().toObject(Group.class);
                            //do something...
                        }
                        Log.e(TAG, "entro a ON_EVENT MEMBER");
                    }
                });
    }

    public void getCode(Code code) {
        this.code = code;
    }

    public void addMember() {
        Toast.makeText(getContext(), "ADD MENBER", Toast.LENGTH_LONG).show();
        ((MainActivity) getActivity()).changeFragmentMember();

    }

    @Override
    public void cellClick(int row, int col) {
        if (col > 0) {
            Member member = members.get(row);
            //Toast.makeText(getContext(), ((member.is_one == true) ? "false" : "true"), Toast.LENGTH_LONG).show();
            showDialogConfirm(member, col);
        }
    }

    public void showDialogConfirm(final Member member, final int col) {
        AlertDialog.Builder builder;

        String message = "";
        final boolean state = updateMemberLesson(member, col, true);
        if (state) { // se cambia en estado de
            message = "Esta seguro de DESMARCAR como lección finalizado?";
        } else {
            message = "Esta seguro de MARCAR como lección finalizado?";
        }
        builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle("Información")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        if (state) {
                            updateMemberLesson(member, col, false);
                        } else {
                            updateMemberLesson(member, col, true);
                        }
                        updateMember(member);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        updateMemberLesson(member, col, state);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void updateMember(Member member) {
        firestoreDB.collection("districts")
                .document(code.district_id)
                .collection("churches")
                .document(code.chruch_id)
                .collection("small_group")
                .document(code.group_id)
                .collection("members")
                .document(member.id)
                .set(member, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Member document updated ");
                        Toast.makeText(getActivity(),
                                "Member document has been updated",
                                Toast.LENGTH_SHORT).show();
                        mTableViewAdapter.notifyDataSetChanged();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding event document", e);
                        Toast.makeText(getActivity(),
                                "Member document could not be added",
                                Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public boolean updateMemberLesson(Member member, int row, boolean state) {
        boolean state_copy = false;
        switch (row) {
            case 1:
                state_copy = member.is_one;
                member.is_one = state;
                break;
            case 2:
                state_copy = member.is_two;
                member.is_two = state;
                break;
            case 3:
                state_copy = member.is_three;
                member.is_three = state;
                break;
            case 4:
                state_copy = member.is_four;
                member.is_four = state;
                break;
            case 5:
                state_copy = member.is_five;
                member.is_five = state;
                break;
            case 6:
                state_copy = member.is_six;
                member.is_six = state;
                break;
            case 7:
                state_copy = member.is_seven;
                member.is_seven = state;
                break;
            case 8:
                state_copy = member.is_eight;
                member.is_eight = state;
                break;
            case 9:
                state_copy = member.is_nine;
                member.is_nine = state;
                break;
            case 10:
                state_copy = member.is_ten;
                member.is_ten = state;
                break;
            case 11:
                state_copy = member.is_eleven;
                member.is_eleven = state;
                break;
            case 12:
                state_copy = member.is_twelve;
                member.is_twelve = state;
                break;
            case 13:
                state_copy = member.is_thirteen;
                member.is_thirteen = state;
                break;
            case 14:
                state_copy = member.is_fourteen;
                member.is_fourteen = state;
                break;
            case 15:
                state_copy = member.is_fifteen;
                member.is_fifteen = state;
                break;
            case 16:
                state_copy = member.is_sixteen;
                member.is_sixteen = state;
                break;
            case 17:
                state_copy = member.is_seventeen;
                member.is_seventeen = state;
                break;
            case 18:
                state_copy = member.is_eigthten;
                member.is_eigthten = state;
                break;
            case 19:
                state_copy = member.is_nineteen;
                member.is_nineteen = state;
                break;
            case 20:
                state_copy = member.is_twenty;
                member.is_twenty = state;
                break;
        }
        return state_copy;
    }
}
