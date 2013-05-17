/**
 * Initialisiert alle Laender und KontinentButtons,
 * mit Icons, Rollover- und SelectedIcons und stellt diese jeweils als
 * Liste fuer andere Klassen zur Verfügung
 * 
 */

package playmyworld.gui;

import java.util.ArrayList;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;

public class ButtonBuilder {

    private ArrayList<ZoomButton> europaButtonList;
    private ArrayList<ZoomButton> kontinentButtonList;
    private ResourceBundle bundle;
    final private String iconDir;
    final private String rollDir;
    final private String selDir;

    public ButtonBuilder() {
        europaButtonList = new ArrayList<ZoomButton>();
        kontinentButtonList = new ArrayList<ZoomButton>();
        bundle = ResourceBundle.getBundle("playmyworld.bundle/charts");
        //Lokale Packagepfade der Icons
        iconDir = "pic/icon/";
        rollDir = "pic/roll/";
        selDir = "pic/sel/";
        initEuLaender();
        initKontinente();
    }

    private void initEuLaender() {
        // Default Icon
        ZoomButton d = new ZoomButton(new ImageIcon(iconDir + "D.png"), "D", bundle.getString("D"));
        ZoomButton p = new ZoomButton(new ImageIcon(iconDir + "PL.png"), "PL", bundle.getString("PL"));
        ZoomButton cz = new ZoomButton(new ImageIcon(iconDir + "CZ.png"), "CZ", bundle.getString("CZ"));
        ZoomButton a = new ZoomButton(new ImageIcon(iconDir + "A.png"), "A", bundle.getString("A"));
        ZoomButton ch = new ZoomButton(new ImageIcon(iconDir + "CH.png"), "CH", bundle.getString("CH"));
        ZoomButton f = new ZoomButton(new ImageIcon(iconDir + "F.png"), "F", bundle.getString("F"));
        ZoomButton b = new ZoomButton(new ImageIcon(iconDir + "B.png"), "B", bundle.getString("B"));
        ZoomButton l = new ZoomButton(new ImageIcon(iconDir + "L.png"), "L", bundle.getString("L"));
        ZoomButton nl = new ZoomButton(new ImageIcon(iconDir + "NL.png"), "NL", bundle.getString("NL"));
        ZoomButton den = new ZoomButton(new ImageIcon(iconDir + "DK.png"), "DK", bundle.getString("DK"));
        ZoomButton uk = new ZoomButton(new ImageIcon(iconDir + "GB.png"), "GB", bundle.getString("GB"));
        ZoomButton ir = new ZoomButton(new ImageIcon(iconDir + "IRL.png"), "IRL", bundle.getString("IRL"));
        ZoomButton esp = new ZoomButton(new ImageIcon(iconDir + "E.png"), "E", bundle.getString("E"));
        ZoomButton por = new ZoomButton(new ImageIcon(iconDir + "P.png"), "P", bundle.getString("P"));
        ZoomButton it = new ZoomButton(new ImageIcon(iconDir + "I.png"), "I", bundle.getString("I"));
        ZoomButton nor = new ZoomButton(new ImageIcon(iconDir + "N.png"), "N", bundle.getString("N"));
        ZoomButton schw = new ZoomButton(new ImageIcon(iconDir + "S.png"), "S", bundle.getString("S"));
        ZoomButton fin = new ZoomButton(new ImageIcon(iconDir + "FIN.png"), "FIN", bundle.getString("FIN"));

        ZoomButton alb = new ZoomButton(new ImageIcon(iconDir + "AL.png"), "AL", bundle.getString("AL"));
        ZoomButton bos_herz = new ZoomButton(new ImageIcon(iconDir + "BIH.png"), "BIH", bundle.getString("BIH"));
        ZoomButton bul = new ZoomButton(new ImageIcon(iconDir + "BG.png"), "BG", bundle.getString("BG"));
        ZoomButton gr = new ZoomButton(new ImageIcon(iconDir + "GR.png"), "GR", bundle.getString("GR"));
        ZoomButton hu = new ZoomButton(new ImageIcon(iconDir + "H.png"), "H", bundle.getString("H"));
        ZoomButton kro = new ZoomButton(new ImageIcon(iconDir + "HR.png"), "HR", bundle.getString("HR"));
        ZoomButton mac = new ZoomButton(new ImageIcon(iconDir + "MK.png"), "MK", bundle.getString("MK"));
        ZoomButton mold = new ZoomButton(new ImageIcon(iconDir + "MD.png"), "MD", bundle.getString("MD"));
        ZoomButton rum = new ZoomButton(new ImageIcon(iconDir + "RO.png"), "RO", bundle.getString("RO"));
        ZoomButton serb = new ZoomButton(new ImageIcon(iconDir + "SCG.png"), "SCG", bundle.getString("SCG"));
        ZoomButton slova = new ZoomButton(new ImageIcon(iconDir + "SK.png"), "SK", bundle.getString("SK"));
        ZoomButton slove = new ZoomButton(new ImageIcon(iconDir + "SLO.png"), "SLO", bundle.getString("SLO"));
        ZoomButton ukr = new ZoomButton(new ImageIcon(iconDir + "UA.png"), "UA", bundle.getString("UA"));
        ZoomButton est = new ZoomButton(new ImageIcon(iconDir + "EST.png"), "EST", bundle.getString("EST"));
        ZoomButton lt = new ZoomButton(new ImageIcon(iconDir + "LT.png"), "LT", bundle.getString("LT"));
        ZoomButton lv = new ZoomButton(new ImageIcon(iconDir + "LV.png"), "LV", bundle.getString("LV"));
        ZoomButton by = new ZoomButton(new ImageIcon(iconDir + "BY.png"), "BY", bundle.getString("BY"));
        

        europaButtonList.add(p);
        europaButtonList.add(d);
        europaButtonList.add(cz);
        europaButtonList.add(a);
        europaButtonList.add(ch);
        europaButtonList.add(f);
        europaButtonList.add(b);
        europaButtonList.add(l);
        europaButtonList.add(nl);
        europaButtonList.add(den);
        europaButtonList.add(uk);
        europaButtonList.add(ir);
        europaButtonList.add(esp);
        europaButtonList.add(por);
        europaButtonList.add(it);
        europaButtonList.add(nor);
        europaButtonList.add(schw);
        europaButtonList.add(fin);
        europaButtonList.add(alb);
        europaButtonList.add(bos_herz);
        europaButtonList.add(bul);
        europaButtonList.add(gr);
        europaButtonList.add(hu);
        europaButtonList.add(kro);
        europaButtonList.add(mac);
        europaButtonList.add(mold);
        europaButtonList.add(rum);
        europaButtonList.add(serb);
        europaButtonList.add(slova);
        europaButtonList.add(slove);
        europaButtonList.add(ukr);
        europaButtonList.add(est);
        europaButtonList.add(lt);
        europaButtonList.add(lv);
        europaButtonList.add(by);

        // Rollover- und Selected Icon
        for (ZoomButton bt : europaButtonList) {
            bt.setRolloverIcon(new ImageIcon(rollDir + bt.getId() + ".png"));
            bt.setSelectedIcon(new ImageIcon(selDir + bt.getId() + ".png"));

        }
    }

    private void initKontinente() {

        ZoomButton eu = new ZoomButton(new ImageIcon("pic/kontinente/europa.png"), "EU", bundle.getString("EU"));
        eu.setRolloverIcon(new ImageIcon("pic/kontinente/europa_bw.png"));

        ZoomButton af = new ZoomButton(new ImageIcon("pic/kontinente/afrika.png"), "AF", bundle.getString("AF"));
        af.setRolloverIcon(new ImageIcon("pic/kontinente/afrika_bw.png"));

        ZoomButton na = new ZoomButton(new ImageIcon("pic/kontinente/nordamerika.png"), "NA", bundle.getString("NA"));
        na.setRolloverIcon(new ImageIcon("pic/kontinente/nordamerika_bw.png"));

        ZoomButton sa = new ZoomButton(new ImageIcon("pic/kontinente/suedamerika.png"), "SA", bundle.getString("SA"));
        sa.setRolloverIcon(new ImageIcon("pic/kontinente/suedamerika_bw.png"));

        ZoomButton as = new ZoomButton(new ImageIcon("pic/kontinente/asien.png"), "AS", bundle.getString("AS"));
        as.setRolloverIcon(new ImageIcon("pic/kontinente/asien_bw.png"));

        ZoomButton au = new ZoomButton(new ImageIcon("pic/kontinente/australien.png"), "OZ", bundle.getString("OZ"));
        au.setRolloverIcon(new ImageIcon("pic/kontinente/australien_bw.png"));

        kontinentButtonList.add(eu);
        kontinentButtonList.add(af);
        kontinentButtonList.add(na);
        kontinentButtonList.add(sa);
        kontinentButtonList.add(as);
        kontinentButtonList.add(au);
    }

    public ArrayList<ZoomButton> getEuropaButtonGroup() {
        return europaButtonList;
    }

    public ArrayList<ZoomButton> getKontinentButtonGroup() {
        return kontinentButtonList;
    }
}
