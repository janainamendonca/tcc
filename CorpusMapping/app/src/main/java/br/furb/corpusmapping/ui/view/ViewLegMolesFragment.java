package br.furb.corpusmapping.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RadioButton;

import br.furb.corpusmapping.ui.common.BodyImageSliderActivity;
import br.furb.corpusmapping.R;
import br.furb.corpusmapping.data.model.SpecificBodyPart;
import br.furb.corpusmapping.util.ImageDrawer;

import static br.furb.corpusmapping.ui.common.BodyImageSliderActivity.PARAM_BODY_PARTS;
import static br.furb.corpusmapping.ui.common.BodyImageSliderActivity.PARAM_IMAGES;

/**
 * Fragment para visualização das pintas das pernas e pés. Permite escolher entre o lado direito e esquerdo
 *
 * @author Janaina Carraro Mendonça Lima
 */
public class ViewLegMolesFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private ImageView imgLegLeftFront;
    private ImageView imgLegLeftBack;
    private ImageView imgFootLeftTop;
    private ImageView imgFootLeftDown;

    private ImageView imgLegRightFront;
    private ImageView imgLegRightBack;
    private ImageView imgFootRightTop;
    private ImageView imgFootRightDown;
    private RadioButton rbLeft;
    private RadioButton rbRight;
    private GridLayout pnLeft;
    private GridLayout pnRight;
    private View view;

    public static ViewLegMolesFragment newInstance() {
        ViewLegMolesFragment fragment = new ViewLegMolesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ViewLegMolesFragment() {
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
        view = inflater.inflate(R.layout.fragment_view_leg_moles, container, false);

        imgLegLeftFront = (ImageView) view.findViewById(R.id.imgLegLeftFront);
        imgLegLeftBack = (ImageView) view.findViewById(R.id.imgLegLeftBack);
        imgFootLeftTop = (ImageView) view.findViewById(R.id.imgFootLeftTop);
        imgFootLeftDown = (ImageView) view.findViewById(R.id.imgFootLeftDown);

        imgLegLeftBack.setOnClickListener(this);
        imgLegLeftFront.setOnClickListener(this);
        imgFootLeftTop.setOnClickListener(this);
        imgFootLeftDown.setOnClickListener(this);

        // desenha as pintas associadas à perna e pé do lado esquerdo
        ImageDrawer.drawPointsOfBodyPart(imgLegLeftFront, SpecificBodyPart.LEFT_LEG_FRONT, R.drawable.perna_esquerda_frente);
        ImageDrawer.drawPointsOfBodyPart(imgLegLeftBack, SpecificBodyPart.LEFT_LEG_BACK, R.drawable.perna_esquerda_atras);
        ImageDrawer.drawPointsOfBodyPart(imgFootLeftDown, SpecificBodyPart.LEFT_FOOT_DOWN, R.drawable.pe_esquerdo_baixo);
        ImageDrawer.drawPointsOfBodyPart(imgFootLeftTop, SpecificBodyPart.LEFT_FOOT_TOP, R.drawable.pe_esquerdo_cima);


        imgLegRightFront = (ImageView) view.findViewById(R.id.imgLegRightFront);
        imgLegRightBack = (ImageView) view.findViewById(R.id.imgLegRightBack);
        imgFootRightTop = (ImageView) view.findViewById(R.id.imgFootRightTop);
        imgFootRightDown = (ImageView) view.findViewById(R.id.imgFootRightDown);

        imgLegRightBack.setOnClickListener(this);
        imgLegRightFront.setOnClickListener(this);
        imgFootRightTop.setOnClickListener(this);
        imgFootRightDown.setOnClickListener(this);

        // desenha as pintas associadas à perna e pé do lado direito
        ImageDrawer.drawPointsOfBodyPart(imgLegRightFront, SpecificBodyPart.RIGHT_LEG_FRONT, R.drawable.perna_direita_frente);
        ImageDrawer.drawPointsOfBodyPart(imgLegRightBack, SpecificBodyPart.RIGHT_LEG_BACK, R.drawable.perna_direita_atras);
        ImageDrawer.drawPointsOfBodyPart(imgFootRightDown, SpecificBodyPart.RIGHT_FOOT_DOWN, R.drawable.pe_direito_baixo);
        ImageDrawer.drawPointsOfBodyPart(imgFootRightTop, SpecificBodyPart.RIGHT_FOOT_TOP, R.drawable.pe_direito_cima);

        rbRight = (RadioButton) view.findViewById(R.id.rbRight);
        rbLeft = (RadioButton) view.findViewById(R.id.rbLeft);

        pnLeft = (GridLayout) view.findViewById(R.id.pnLeft);
        pnRight = (GridLayout) view.findViewById(R.id.pnRight);

        rbLeft.setChecked(true);

        rbLeft.setOnCheckedChangeListener(this);
        rbRight.setOnCheckedChangeListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(getActivity(), BodyImageSliderActivity.class);
        i.putExtra(BodyImageSliderActivity.PARAM_IS_TO_ASSOCIATE, false);
        switch (v.getId()) {

            // Esquerda
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

            // Direita

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
        getActivity().startActivity(i);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (buttonView.getId() == R.id.rbLeft) {
            if (rbLeft.isChecked()) {
                rbRight.setChecked(false);
                pnLeft.setVisibility(View.VISIBLE);
                pnRight.setVisibility(View.INVISIBLE);
            }
        } else {
            if (rbRight.isChecked()) {
                rbLeft.setChecked(false);
                pnRight.setVisibility(View.VISIBLE);
                pnLeft.setVisibility(View.INVISIBLE);
            }
        }
    }
}
