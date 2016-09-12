package pma.vinko.legendtracker.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Random;
import java.util.concurrent.ExecutionException;

import pma.vinko.legendtracker.R;
import pma.vinko.legendtracker.asynctasks.GetData;
import pma.vinko.legendtracker.dal.UserReader;
import pma.vinko.legendtracker.dal.UserReaderContract;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UsersFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UsersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UsersFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public UsersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UsersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UsersFragment newInstance(String param1, String param2) {
        UsersFragment fragment = new UsersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_users, container, false);
        TableLayout table = (TableLayout) rootView.findViewById(R.id.usersTable);
        UserReader reader = new UserReader(getActivity());
        Cursor cursor = reader.getAll();

        if (cursor.moveToFirst()){
            do{
                String name = cursor.getString(cursor.getColumnIndex(UserReaderContract.COLUMN_NAME_TITLE));
                String id = cursor.getString(cursor.getColumnIndex(UserReaderContract.COLUMN_NAME_ID));
                TableRow tr = createTableRow();
                tr.addView(createTextView(name));
                tr.addView(createTextView(id));
                table.addView(tr);

                // do what ever you want here
            }while(cursor.moveToNext());
        }
        cursor.close();


        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Add friend");

                // Set up the input
                final EditText input = new EditText(getActivity());
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                final TextView error = new TextView(getActivity());
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                error.setVisibility(View.INVISIBLE);
                error.setTextColor(Color.RED);
                error.setTextSize(11);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {}
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                builder.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        String name =  input.getText().toString();

                        if(!name.equals("") && name != null) {
                            String url = "https://eune.api.pvp.net/api/lol/eune/v1.4/summoner/by-name/skilledcex?api_key=RGAPI-CBA5D9B2-7ED7-42FB-A7F0-D312B30965EA";
                            try {
                                JSONObject json = new JSONObject(new GetData().execute(url).get());

                                if (json.has("name")) {
                                    UserReader reader = new UserReader(getActivity());
                                    reader.insertSettings(json.getString("id"), json.getString("name"));

                                }
                                else{
                                    error.setVisibility(View.VISIBLE);
                                    error.setText("Player not found. Please, insert valid player name.");
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            error.setVisibility(View.VISIBLE);
                            error.setText("Please, insert player name.");
                        }
                    }
                });
            }
        });

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

   /* @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/

    private TextView createTextView(String text){

        TableRow tr = createTableRow();
        TextView textView = new TextView(getActivity());
        textView.setPadding(12,12,12,12);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        // textView.setTextColor(Color.YELLOW);
        textView.setText(text);

        return textView;
    }

    private TableRow createTableRow(){
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        TableRow tr = new TableRow(getActivity());
        tr.setLayoutParams(lp);
        tr.setPadding(0,15,0,15);
        return tr;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
