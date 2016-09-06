package pma.vinko.legendtracker.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import pma.vinko.legendtracker.ChampionDetails;
import pma.vinko.legendtracker.R;
import pma.vinko.legendtracker.StaticDataActivity;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChampionsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChampionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChampionsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ChampionsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChampionsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChampionsFragment newInstance(String param1, String param2) {
        ChampionsFragment fragment = new ChampionsFragment();
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

        String url = "http://ddragon.leagueoflegends.com/cdn/6.17.1/data/en_US/champion.json";
        new ChampionsOperation().execute(url);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_champions, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    private class ChampionsOperation extends AsyncTask<String, Void, Void> {

        final HttpClient client = new DefaultHttpClient();
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        String data, content;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog.setTitle("Please wait...");
            progressDialog.show();
        }


        @Override
        protected Void doInBackground(String... params) {
            InputStream inputStream = null;
            String result = "";

            try{
                HttpResponse httpResponse = client.execute(new HttpGet(params[0]));

                inputStream = httpResponse.getEntity().getContent();

                if(inputStream != null){
                    content = readStream(inputStream);
                }
                else{
                    content = "Oops, something went wrong";
                }

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @NonNull
        private String readStream(InputStream stream) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line = "";
            StringBuilder result = new StringBuilder();

            while((line = reader.readLine()) != null){
                result.append(line);
            }

            stream.close();
            return  result.toString();
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

            try {
                JSONObject json = new JSONObject(content).getJSONObject("data");
                StringBuilder champions = new StringBuilder();
                Iterator<String> keys = json.keys();
                TableLayout tl = (TableLayout) getView().findViewById(R.id.championsTable);

                while (keys.hasNext()) {
                    //get champ details
                    final JSONObject jsonChampion = json.getJSONObject(keys.next());
                    //instantiate table row
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                    TableRow tr = new TableRow(getActivity());
                    tr.setLayoutParams(lp);

                    //add champ details
                    tr.addView(getTextView(jsonChampion, "name","title"));

                    Button button = new Button(getActivity());

                    button.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    button.setTextColor(Color.YELLOW);
                    button.setText("test");

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Intent intent = new Intent(getActivity(), ChampionDetails.class);
                                Bundle b = new Bundle();
                                b.putString("championId", jsonChampion.getString("id"));
                                intent.putExtras(b);
                                startActivity(intent);
                                getActivity().finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    tr.addView(button);
                    tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

        private View getTextView(JSONObject json, String... propertyNames) throws JSONException {

            StringBuilder text = new StringBuilder();

            for(int i = 0; i < propertyNames.length; i++) {
                text.append(json.getString(propertyNames[i]));

                if(i < propertyNames.length - 1 )
                    text.append(" ");

            }

            return createTextView(text.toString());
        }

        private View createTextView(String text) {

            TextView textView = new TextView(getActivity());

            textView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            textView.setTextColor(Color.YELLOW);
            textView.setText(text);

            return textView;
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
}
