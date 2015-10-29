package br.furb.corpusmapping.capture;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import br.furb.corpusmapping.R;
import br.furb.corpusmapping.SpecificBodyPart;
import br.furb.corpusmapping.util.ImageDrawer;

import static br.furb.corpusmapping.capture.BodyImageSliderActivity.PARAM_BODY_PARTS;
import static br.furb.corpusmapping.capture.BodyImageSliderActivity.PARAM_IMAGES;

/**
 * Fragment apresentado para o usuário selecionar a região (ponto) da perna esquerda onde deseja associar a imagem capturada.
 */
public class SelectionLegLeftFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;

    private ImageView imgLegFront;
    private ImageView imgLegBack;
    private ImageView imgFootTop;
    private ImageView imgFootDown;

    public static SelectionLegLeftFragment newInstance() {
        SelectionLegLeftFragment fragment = new SelectionLegLeftFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SelectionLegLeftFragment() {
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
        View view = inflater.inflate(R.layout.fragment_selection_leg_left, container, false);

        imgLegFront = (ImageView) view.findViewById(R.id.imgLegLeftFront);
        imgLegBack = (ImageView) view.findViewById(R.id.imgLegLeftBack);
        imgFootTop = (ImageView) view.findViewById(R.id.imgFootLeftTop);
        imgFootDown = (ImageView) view.findViewById(R.id.imgFootLeftDown);

        imgLegBack.setOnClickListener(this);
        imgLegFront.setOnClickListener(this);
        imgFootTop.setOnClickListener(this);
        imgFootDown.setOnClickListener(this);

        ImageDrawer.drawPoints(imgLegFront, SpecificBodyPart.LEFT_LEG_FRONT, R.drawable.perna_esquerda_frente);
        ImageDrawer.drawPoints(imgLegBack, SpecificBodyPart.LEFT_LEG_BACK, R.drawable.perna_esquerda_atras);
        ImageDrawer.drawPoints(imgFootDown, SpecificBodyPart.LEFT_FOOT_DOWN, R.drawable.pe_esquerdo_baixo);
        ImageDrawer.drawPoints(imgFootTop, SpecificBodyPart.LEFT_FOOT_TOP, R.drawable.pe_esquerdo_cima);
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
        Intent i = new Intent(getActivity(), BodyImageSliderActivity.class);
        switch (v.getId()) {
            case R.id.imgLegLeftFront:
                i.putExtra(PARAM_IMAGES, new int[]{R.drawable.perna_esquerda_frente, R.drawable.perna_esquerda_atras, R.drawable.pe_esquerdo_cima, R.drawable.pe_esquerdo_baixo});
                i.putExtra(PARAM_BODY_PARTS, new String[]{SpecificBodyPart.LEFT_LEG_FRONT.name(), SpecificBodyPart.LEFT_LEG_BACK.name(), SpecificBodyPart.LEFT_FOOT_TOP.name(), SpecificBodyPart.LEFT_FOOT_DOWN.name()});
                break;
            case R.id.imgLegLeftBack:
                i.putExtra(PARAM_IMAGES, new int[]{R.drawable.perna_esquerda_atras, R.drawable.perna_esquerda_frente, R.drawable.pe_esquerdo_cima, R.drawable.pe_esquerdo_baixo});
                i.putExtra(PARAM_BODY_PARTS, new String[]{SpecificBodyPart.LEFT_LEG_BACK.name(), SpecificBodyPart.LEFT_LEG_FRONT.name(), SpecificBodyPart.LEFT_FOOT_TOP.name(), SpecificBodyPart.LEFT_FOOT_DOWN.name()});
                break;
            case R.id.imgFootLeftTop:
                i.putExtra(PARAM_IMAGES, new int[]{R.drawable.pe_esquerdo_cima, R.drawable.pe_esquerdo_baixo, R.drawable.perna_esquerda_atras, R.drawable.perna_esquerda_frente});
                i.putExtra(PARAM_BODY_PARTS, new String[]{SpecificBodyPart.LEFT_FOOT_TOP.name(), SpecificBodyPart.LEFT_FOOT_DOWN.name(), SpecificBodyPart.LEFT_LEG_BACK.name(), SpecificBodyPart.LEFT_LEG_FRONT.name()});

                break;
            case R.id.imgFootLeftDown:
                i.putExtra(PARAM_IMAGES, new int[]{R.drawable.pe_esquerdo_baixo, R.drawable.pe_esquerdo_cima, R.drawable.perna_esquerda_atras, R.drawable.perna_esquerda_frente});
                i.putExtra(PARAM_BODY_PARTS, new String[]{SpecificBodyPart.LEFT_FOOT_DOWN.name(), SpecificBodyPart.LEFT_FOOT_TOP.name(), SpecificBodyPart.LEFT_LEG_BACK.name(), SpecificBodyPart.LEFT_LEG_FRONT.name()});
                break;
        }
        getActivity().startActivityForResult(i, SelectBodyPartActivity.REQUEST_CODE);
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
