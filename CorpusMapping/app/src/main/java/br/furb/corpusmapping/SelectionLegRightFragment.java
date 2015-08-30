package br.furb.corpusmapping;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import static br.furb.corpusmapping.ImageSliderActivity.PARAM_IMAGES;

public class SelectionLegRightFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;

    private ImageView imgLegFront;
    private ImageView imgLegBack;
    private ImageView imgFootTop;
    private ImageView imgFootDown;

    public static SelectionLegRightFragment newInstance() {
        SelectionLegRightFragment fragment = new SelectionLegRightFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SelectionLegRightFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selection_leg_right, container, false);

        imgLegFront = (ImageView) view.findViewById(R.id.imgLegRightFront);
        imgLegBack = (ImageView) view.findViewById(R.id.imgLegRightBack);
        imgFootTop = (ImageView) view.findViewById(R.id.imgFootRightTop);
        imgFootDown = (ImageView) view.findViewById(R.id.imgFootRightDown);

        imgLegBack.setOnClickListener(this);
        imgLegFront.setOnClickListener(this);
        imgFootTop.setOnClickListener(this);
        imgFootDown.setOnClickListener(this);
        return view;
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
        Intent i = new Intent(getActivity(), ImageSliderActivity.class);
        switch (v.getId()) {
            case R.id.imgLegRightFront:
                i.putExtra(PARAM_IMAGES, new int[]{R.drawable.perna_direita_frente, R.drawable.perna_direita_atras, R.drawable.pe_direito_cima, R.drawable.pe_direito_baixo});
                break;
            case R.id.imgLegRightBack:
                i.putExtra(PARAM_IMAGES, new int[]{R.drawable.perna_direita_atras, R.drawable.perna_direita_frente, R.drawable.pe_direito_cima, R.drawable.pe_direito_baixo});
                break;
            case R.id.imgFootRightTop:
                i.putExtra(PARAM_IMAGES, new int[]{R.drawable.pe_direito_cima, R.drawable.pe_direito_baixo, R.drawable.perna_direita_atras, R.drawable.perna_direita_frente});
                break;
            case R.id.imgFootRightDown:
                i.putExtra(PARAM_IMAGES, new int[]{R.drawable.pe_direito_baixo, R.drawable.pe_direito_cima, R.drawable.perna_direita_atras, R.drawable.perna_direita_frente});
                break;

        }
        getActivity().startActivity(i);
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


}
