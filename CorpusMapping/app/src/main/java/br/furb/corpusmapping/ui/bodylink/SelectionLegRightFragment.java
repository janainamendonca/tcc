package br.furb.corpusmapping.ui.bodylink;

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
import br.furb.corpusmapping.data.model.SpecificBodyPart;
import br.furb.corpusmapping.ui.common.BodyImageSliderActivity;
import br.furb.corpusmapping.util.ImageDrawer;

import static br.furb.corpusmapping.ui.common.BodyImageSliderActivity.PARAM_BODY_PARTS;
import static br.furb.corpusmapping.ui.common.BodyImageSliderActivity.PARAM_IMAGES;

/**
 * Fragment apresentado para o usuário selecionar a região (ponto) da perna direita onde deseja associar a imagem capturada.
 * @author Janaina Carraro Mendonça Lima
 */
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

        ImageDrawer.drawPointsOfBodyPart(imgLegFront, SpecificBodyPart.RIGHT_LEG_FRONT, R.drawable.perna_esquerda_frente);
        ImageDrawer.drawPointsOfBodyPart(imgLegBack, SpecificBodyPart.RIGHT_LEG_BACK, R.drawable.perna_esquerda_atras);
        ImageDrawer.drawPointsOfBodyPart(imgFootDown, SpecificBodyPart.RIGHT_FOOT_DOWN, R.drawable.pe_esquerdo_baixo);
        ImageDrawer.drawPointsOfBodyPart(imgFootTop, SpecificBodyPart.RIGHT_FOOT_TOP, R.drawable.pe_esquerdo_cima);
        
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
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
            case R.id.imgLegRightFront:
                i.putExtra(PARAM_IMAGES, new int[]{R.drawable.perna_direita_frente, R.drawable.perna_direita_atras, R.drawable.pe_direito_cima, R.drawable.pe_direito_baixo});
                i.putExtra(PARAM_BODY_PARTS, new String[]{SpecificBodyPart.RIGHT_LEG_FRONT.name(), SpecificBodyPart.RIGHT_LEG_BACK.name(), SpecificBodyPart.RIGHT_FOOT_TOP.name(), SpecificBodyPart.RIGHT_FOOT_DOWN.name()});
                break;
            case R.id.imgLegRightBack:
                i.putExtra(PARAM_IMAGES, new int[]{R.drawable.perna_direita_atras, R.drawable.perna_direita_frente, R.drawable.pe_direito_cima, R.drawable.pe_direito_baixo});
                i.putExtra(PARAM_BODY_PARTS, new String[]{SpecificBodyPart.RIGHT_LEG_BACK.name(), SpecificBodyPart.RIGHT_LEG_FRONT.name(), SpecificBodyPart.RIGHT_FOOT_TOP.name(), SpecificBodyPart.RIGHT_FOOT_DOWN.name()});
                break;
            case R.id.imgFootRightTop:
                i.putExtra(PARAM_IMAGES, new int[]{R.drawable.pe_direito_cima, R.drawable.pe_direito_baixo, R.drawable.perna_direita_atras, R.drawable.perna_direita_frente});
                i.putExtra(PARAM_BODY_PARTS, new String[]{SpecificBodyPart.RIGHT_FOOT_TOP.name(), SpecificBodyPart.RIGHT_FOOT_DOWN.name(), SpecificBodyPart.RIGHT_LEG_BACK.name(), SpecificBodyPart.RIGHT_LEG_FRONT.name()});
                break;
            case R.id.imgFootRightDown:
                i.putExtra(PARAM_IMAGES, new int[]{R.drawable.pe_direito_baixo, R.drawable.pe_direito_cima, R.drawable.perna_direita_atras, R.drawable.perna_direita_frente});
                i.putExtra(PARAM_BODY_PARTS, new String[]{SpecificBodyPart.RIGHT_FOOT_DOWN.name(), SpecificBodyPart.RIGHT_FOOT_TOP.name(), SpecificBodyPart.RIGHT_LEG_BACK.name(), SpecificBodyPart.RIGHT_LEG_FRONT.name()});
                break;

        }
        getActivity().startActivityForResult(i, SelectBodyPartActivity.REQUEST_CODE);
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }


}
