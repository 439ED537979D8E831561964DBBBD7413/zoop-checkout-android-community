package com.zoop.checkout.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.zoop.zoopandroidsdk.terminal.DeviceSelectionListener;
import com.zoop.zoopandroidsdk.TerminalListManager;
import com.zoop.zoopandroidsdk.commons.APIParameters;
import com.zoop.zoopandroidsdk.commons.ZLog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by mainente on 17/04/15.
 */
public class FlipperConfigPinPadActivity extends ZoopFlipperPane implements DeviceSelectionListener {
    Button btnBuy;
    private static FlipperConfigPinPadActivity instance = null;

    public static boolean bShowQuickInstructions = false;
    TerminalListManager terminalListManager;
    String sTerminalListMoreInfo;
    SimpleAdapter adapter;
    ListView lv;
    ArrayList<HashMap<String, Object>> arrayListZoopTerminalDeviceListForUI;
    LinearLayout linearLayoutTerminalSearchPane;

    public static FlipperConfigPinPadActivity getInstance() {
        if (null == instance) {
            instance = new FlipperConfigPinPadActivity();
        }
        return instance;
    }

    public int getLayoutResourceId() {
        return R.layout.welcome_search_terminal_pane;
    }

    @Override
    public void onFlip() {

        APIParameters ap = APIParameters.getInstance();
        linearLayoutTerminalSearchPane = (LinearLayout)currentActivity.findViewById(R.id.linearLayoutTerminalSearchPane);

        adapter = null;
        String sTerminalDiscoveryInstructions = getCurrentActivity().getResources().getString(R.string.product_name)+" vai configurar a sua maquininha automaticamente. Se você não tem uma maquininha, clique próximo.\nPara configurar, siga os passos abaixo:\n" +
                "1) Desligue a maquininha (se estiver ligada).\n" +
                "2) Ligue a maquininha e aguarde a inicialização até que apareça o logotipo ou PAX D180.\n" +
                "3) Na maquininha, aperte o botão 0 (zero) e aguarde que ela seja encontrada. Se não funcionar, clique em anterior e tente novamente. \n\n";
        TextView textViewTerminalDiscoveryInstructions = (TextView)getCurrentActivity().findViewById(R.id.textViewTerminalDiscoveryInstructions);
        textViewTerminalDiscoveryInstructions.setText(sTerminalDiscoveryInstructions);
        terminalListManager = new TerminalListManager(this, getCurrentActivity().getApplicationContext());
        terminalListManager.startTerminalsDiscovery();


    }



    @Override
    public void onDestroy() {
        terminalListManager.finishTerminalDiscovery();
        super.onDestroy();
    }



    public void showAutoSelectedZoopTerminal(JSONObject joZoopTerminal) {
        TextView textViewAutomaticZoopTerminalConfigurationInstructions = (TextView)getCurrentActivity().findViewById(R.id.textViewTerminalDiscoveryInstructions);
        textViewAutomaticZoopTerminalConfigurationInstructions.setVisibility(View.GONE);

        String sCurrentlySelectedZoopTerminalManufacturerName = null;
        String sCurrentlySelectedTerminal = null;

        try {
            //sCurrentlySelectedZoopTerminalManufacturerName = ;
            sCurrentlySelectedTerminal = getCurrentActivity().getResources().getString(R.string.label_currently_selected_terminal_on_wizard);

            sCurrentlySelectedTerminal = sCurrentlySelectedTerminal.replace("[manufacturer]", joZoopTerminal.getString("manufacturer"));
            sCurrentlySelectedTerminal = sCurrentlySelectedTerminal.replace("[btName]", joZoopTerminal.getString("name"));
            ((TextView) getCurrentActivity().findViewById(R.id.textViewCurrentlySelectedTerminal)).setText(sCurrentlySelectedTerminal);
            ((TextView) getCurrentActivity().findViewById(R.id.textViewCurrentlySelectedTerminal)).setVisibility(View.VISIBLE);
            linearLayoutTerminalSearchPane.setVisibility(View.GONE);

            //sTerminalListMoreInfo = getCurrentActivity().getResources().getString(R.string.text_select_available_terminal);
            //((TextView) getCurrentActivity().findViewById(R.id.textViewLabelTerminalList)).setText(getCurrentActivity().getResources().getString(R.string.label_select_available_terminal));

            String terminalName = "A maquininha : " + joZoopTerminal.getString("name") + " foi configurada. Clique em Próximo. ";

            String confTermInfo="Para configurar outra maquininha manualmente, clique em Configurações -> Maquininhas";

            TextView terminalDiscoveryinfoConf = (TextView) getCurrentActivity().findViewById(R.id.textViewSelectedTerminalMoreInformation);
            terminalDiscoveryinfoConf.setVisibility(View.VISIBLE);
            terminalDiscoveryinfoConf.setText(confTermInfo);
//            terminalDiscovery.setText(terminalName);
        }
        catch (Exception e) {
            ((TextView) getCurrentActivity().findViewById(R.id.textViewCurrentlySelectedTerminal)).setText(getCurrentActivity().getResources().getString(R.string.label_no_terminal_selected));
            ((TextView) getCurrentActivity().findViewById(R.id.textViewCurrentlySelectedTerminal)).setVisibility(View.VISIBLE);

        }

    }

    @Override
    public void showDeviceListForUserSelection(final Vector<JSONObject> vectorZoopTerminals) {
        try {
            if (vectorZoopTerminals.size() > 0) {
                terminalListManager.requestZoopDeviceSelection(vectorZoopTerminals.get(0));
            }

        }
        catch (Exception e) {
            ZLog.exception(300064, e);
        }
    }

    @Override
    public void updateDeviceListForUserSelecion(JSONObject joNewlyFoundZoopDevice, Vector<JSONObject> vectorZoopTerminals, int iNewlyFoundDeviceIndex) {
        try {
            terminalListManager.requestZoopDeviceSelection(joNewlyFoundZoopDevice);

        } catch (Exception e) {
            ZLog.exception(677541, e);
        }
    }

    @Override
    public void bluetoothIsNotEnabledNotification() {
        terminalListManager.enableDeviceBluetoothAdapter();
    }

    @Override
    public void deviceSelectedResult(JSONObject joZoopSelectedDevice, Vector<JSONObject> vectorAllAvailableZoopTerminals, int iSelectedDeviceIndex) {
        showAutoSelectedZoopTerminal(joZoopSelectedDevice);
    }

    public void stopDiscovery(){
        terminalListManager.finishTerminalDiscovery();
    }

}
