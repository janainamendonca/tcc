package br.furb.corpusmapping;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import br.furb.corpusmapping.util.BBox;
import br.furb.corpusmapping.util.ImageDrawer;

public class SelectionHeadFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ImageView imgHeadFront;
    private TextView txtX;
    private TextView txtY;
    private ImageView imgHeadBack;
    private ImageView imgHeadLeft;
    private ImageView imgHeadRight;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SelectionHeadFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SelectionHeadFragment newInstance(String param1, String param2) {
        SelectionHeadFragment fragment = new SelectionHeadFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SelectionHeadFragment() {
        // Required empty public constructor
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
        View view = inflater.inflate(R.layout.fragment_selection_head, container, false);

        imgHeadFront = (ImageView) view.findViewById(R.id.imgHeadFront);
        imgHeadBack = (ImageView) view.findViewById(R.id.imgHeadBack);
        imgHeadLeft = (ImageView) view.findViewById(R.id.imgHeadLeft);
        imgHeadRight = (ImageView) view.findViewById(R.id.imgHeadRight);

        imgHeadFront.setOnTouchListener(new AssociateBodyPartTouchListener(R.drawable.cabeca_frente, getHeadFrontBBox()));
        imgHeadBack.setOnTouchListener(new AssociateBodyPartTouchListener(R.drawable.cabeca_atras));
        imgHeadLeft.setOnTouchListener(new AssociateBodyPartTouchListener(R.drawable.cabeca_esquerda));
        imgHeadRight.setOnTouchListener(new AssociateBodyPartTouchListener(R.drawable.cabeca_direita));

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
       /* try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgHeadFront:


                break;

        }
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
        public void onFragmentInteraction(Uri uri);
    }

    public BBox[] getHeadFrontBBox() {
        BBox[] bbox = new BBox[3];
        bbox[0] = (new BBox(50, 250, 230, 370));// orelha p/ baixo
        bbox[1] = (new BBox(10, 160, 275, 250)); // orelhas
        bbox[2] = (new BBox(20, 15, 260, 250)); // testa
        return bbox;
    }


}
