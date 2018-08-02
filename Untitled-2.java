package com.atpco.process.faredisp.extract;

import com.atpco.entity.od.faredisp.DispCat015TBL900;
import com.atpco.entity.od.faredisp.DispFareQueryOutput;
import com.atpco.model.TCat015;
import com.atpco.model.TCat015Segs;
import com.atpco.model.TTbl994;
import com.atpco.model.TTbl996;
import com.atpco.process.faredisp.extract.util.DispExtConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.*;

/*
 * Class History
 * Author            Date                    Version           Comments
 * WangWenlong       2018-04-02              1.00              初始化
 */
/**
 * Fare Display For Category 15 & Its Segments
 *
 * @see IDispExtractor
 */
@Component("cat015DispExtractor")
public class Cat015DispExtractor implements IDispExtractor<TCat015, DispCat015TBL900> {

    @Inject
    MessageSource messageSource;

    @Override
    public String dispDetailExtract(TCat015 cat015, DispCat015TBL900 tbl900) {
        // Need Have
        String origCtry = "CN";
        String destCtry = "US";
        String publishCxr = "MU";
        Locale locale = tbl900.getLocale();

        LocaleMessageSource localeMessageSource = new LocaleMessageSource(messageSource, locale);

        StringBuilder cat015Msge = new StringBuilder();

//        localeMessageSource.newCat015MsgLineL0(cat015Msge, "cat015.title");

        TTbl994 tbl994 = null == tbl900.getTbl994() ? new TTbl994() : tbl900.getTbl994();

        String reserve = StringUtils.trimToEmpty(tbl994.getReserve()); // 存根出票限制：空：不适用；Y：可以；N：不可以；
        Date tvleffdt = tbl994.getTvleffdt(); // 旅行最早日期
        Date tvldisdt = tbl994.getTvldisdt(); // 旅行最晚日期
        Date tkteffdt = tbl994.getTkteffdt(); // 出票最早日期
        Date tktdisdt = tbl994.getTktdisdt(); // 出票最晚日期
        Date rsveffdt = tbl994.getRsveffdt(); // 存根最早日期
        Date rsvdisdt = tbl994.getRsvdisdt(); // 存根最晚日期

        Date eresDate = cat015.getEresdate(); // 可接受的订票最早日期
        Date lresDate = cat015.getLresdate(); // 可接受的订票最晚日期
        Date etktDate = cat015.getEtktdate(); // 可接受的出票最早日期
        Date ltktDate = cat015.getLtktdate(); // 可接受的出票最晚日期

        if (null != tkteffdt || null != tktdisdt) {
            etktDate = tkteffdt;
            ltktDate = tktdisdt;
        }
        if (null != rsveffdt || null != rsvdisdt) {
            eresDate = rsveffdt;
            lresDate = rsvdisdt;
        }

        if (null != eresDate || null != lresDate) {
            localeMessageSource.newCat015MsgLineL3(cat015Msge, "cat015.temp.resdate", new Object[]{eresDate, lresDate});
        }
        if (null != etktDate || null != ltktDate) {
            localeMessageSource.newCat015MsgLineL3(cat015Msge, "cat015.temp.tktdate", new Object[]{etktDate, ltktDate});
        }
        if (null != tvleffdt || null != tvldisdt) {
            localeMessageSource.newCat015MsgLineL3(cat015Msge, "cat015.temp.tvldate", new Object[]{tvleffdt, tvldisdt});
        }

        String ctry = StringUtils.trimToEmpty(cat015.getCtry()); // 国家限制；空：无限制；O：必须出发国家；D：必须到达国家；B：两者都可以；
        String res = StringUtils.trimToEmpty(cat015.getRes());// 居民限制；空：无限制；P：固定居民；N：非居民；C：公民；
        String cxrSale = StringUtils.trimToEmpty(cat015.getCxrsale()); // 售票航空公司限制；空：无限制；X：必须被「CXROTH」销售；C：必须被GDS/CRS「CXROTH」销售；
        String cxrOth = StringUtils.trimToEmpty(cat015.getCxroth()); // 可以售票的航空公司代码
        String cxrVal = StringUtils.trimToEmpty(cat015.getCxrval()); // 检票航空公司限制；空：无限制；S：必须被发布航空公司检票；O：必须被「CXROTH」检票；
        String cxrSeg = StringUtils.trimToEmpty(cat015.getCxrseg()); // 航段承运商限制：空：无限制；N：此票所有航段不允许非发布航空公司；F：此运价所有航段不允许非发布航空公司；
        String taSale = StringUtils.trimToEmpty(cat015.getTasale()); // 旅行社销售限制：Y：可能只可以；N：不可以；
        String taSel = StringUtils.trimToEmpty(cat015.getTasel()); // 旅行社可能不可以，可以被指定旅行社销售

        if (StringUtils.isNotBlank(ctry)) {
            char ctryCode = ctry.charAt(0);
            switch (ctryCode) {
                case 'O':
                    localeMessageSource.newCat015MsgLineL3(cat015Msge, "cat015.temp.ctry.o", new Object[]{origCtry});
                    break;
                case 'D':
                    localeMessageSource.newCat015MsgLineL3(cat015Msge, "cat015.temp.ctry.d", new Object[]{destCtry});
                    break;
                case 'B':
                    localeMessageSource.newCat015MsgLineL3(cat015Msge, "cat015.temp.ctry.b", new Object[]{origCtry, destCtry});
                    break;
                default:
                    break;
            }
        }
        if (StringUtils.isNotBlank(res)) {
            char resCode = res.charAt(0);
            switch (resCode) {
                case 'P':
                    localeMessageSource.newCat015MsgLineL3(cat015Msge, "cat015.temp.res.p");
                    break;
                case 'N':
                    localeMessageSource.newCat015MsgLineL3(cat015Msge, "cat015.temp.res.n");
                    break;
                case 'C':
                    localeMessageSource.newCat015MsgLineL3(cat015Msge, "cat015.temp.res.c");
                    break;
                default:
                    break;
            }
        }
        if (StringUtils.isNotBlank(cxrSale)) {
            char cxrSaleCode = cxrSale.charAt(0);
            switch (cxrSaleCode) {
                case 'X':
                    localeMessageSource.newCat015MsgLineL3(cat015Msge, "cat015.temp.cxrsale.x", new Object[]{publishCxr, cxrOth});
                    break;
                case 'C':
                    localeMessageSource.newCat015MsgLineL3(cat015Msge, "cat015.temp.cxrsale.c", new Object[]{cxrOth});
                    break;
                default:
                    break;
            }
        }
        if (StringUtils.isNotBlank(cxrVal)) {
            char cxrValCode = cxrVal.charAt(0);
            switch (cxrValCode) {
                case 'S':
                    localeMessageSource.newCat015MsgLineL3(cat015Msge, "cat015.temp.cxrval.s", new Object[]{publishCxr, cxrOth});
                    break;
                case 'O':
                    localeMessageSource.newCat015MsgLineL3(cat015Msge, "cat015.temp.cxrval.o", new Object[]{cxrOth});
                    break;
                default:
                    break;
            }
        }
        if (StringUtils.isNotBlank(cxrSeg)) {
            char cxrSegCode = cxrSeg.charAt(0);
            switch (cxrSegCode) {
                case 'N':
                    localeMessageSource.newCat015MsgLineL3(cat015Msge, "cat015.temp.cxrseg.n", new Object[]{publishCxr, cxrOth});
                    break;
                case 'F':
                    localeMessageSource.newCat015MsgLineL3(cat015Msge, "cat015.temp.cxrseg.f", new Object[]{publishCxr, cxrOth});
                    break;
                default:
                    break;
            }
        }
        if (StringUtils.isNotBlank(taSale)) {
            char taSaleCode = taSale.charAt(0);
            switch (taSaleCode) {
                case 'Y':
                    localeMessageSource.newCat015MsgLineL3(cat015Msge, "cat015.temp.tasale.y");
                    break;
                case 'N':
                    localeMessageSource.newCat015MsgLineL3(cat015Msge, "cat015.temp.tasale.n");
                    break;
                default:
                    break;
            }
        }
        if (StringUtils.isNotBlank(taSel)) {
            localeMessageSource.newCat015MsgLineL3(cat015Msge, "cat015.temp.tasel", new Object[]{taSel});
        }

        String fopca = StringUtils.trimToEmpty(cat015.getFopca()); // 现金支付限制；X：不可以；其他：可以；
        String fopck = StringUtils.trimToEmpty(cat015.getFopck()); // 支票支付限制；X：不可以；其他：可以；
        String fopcr = StringUtils.trimToEmpty(cat015.getFopcr()); // 信用卡支付限制；X：不可以；其他：可以；
        String foptr = StringUtils.trimToEmpty(cat015.getFoptr()); // 政府交通补贴支付限制；X：不可以；其他：可以；

        String fopXs = StringUtils.EMPTY;
        String fopYs = StringUtils.EMPTY;
        if (StringUtils.isNotBlank(fopca) && fopca.charAt(0) == 'X') {
            fopXs = fopXs.concat(DispExtConstants.SPACE).concat(localeMessageSource.newCat015Msg("cat015.temp.fop.ca"));
        } else {
            fopYs = fopYs.concat(DispExtConstants.SPACE).concat(localeMessageSource.newCat015Msg("cat015.temp.fop.ca"));
        }
        if (StringUtils.isNotBlank(fopck) && fopck.charAt(0) == 'X') {
            fopXs = fopXs.concat(DispExtConstants.SPACE).concat(localeMessageSource.newCat015Msg("cat015.temp.fop.ck"));
        } else {
            fopYs = fopYs.concat(DispExtConstants.SPACE).concat(localeMessageSource.newCat015Msg("cat015.temp.fop.ck"));
        }
        if (StringUtils.isNotBlank(fopcr) && fopcr.charAt(0) == 'X') {
            fopXs = fopXs.concat(DispExtConstants.SPACE).concat(localeMessageSource.newCat015Msg("cat015.temp.fop.cr"));
        } else {
            fopYs = fopYs.concat(DispExtConstants.SPACE).concat(localeMessageSource.newCat015Msg("cat015.temp.fop.cr"));
        }
        if (StringUtils.isNotBlank(foptr) && foptr.charAt(0) == 'X') {
            fopXs = fopXs.concat(DispExtConstants.SPACE).concat(localeMessageSource.newCat015Msg("cat015.temp.fop.tr"));
        } else {
            fopYs = fopYs.concat(DispExtConstants.SPACE).concat(localeMessageSource.newCat015Msg("cat015.temp.fop.tr"));
        }
        if (StringUtils.isNotBlank(fopYs)) {
            localeMessageSource.newCat015MsgLineL3(cat015Msge, "cat015.temp.fop.y", new Object[]{fopYs});
        }
        if (StringUtils.isNotBlank(fopXs)) {
            localeMessageSource.newCat015MsgLineL3(cat015Msge, "cat015.temp.fop.x", new Object[]{fopXs});
        }

        String curCtry = StringUtils.trimToEmpty(cat015.getCurctry()); // 售票国家限制；空：无限制；O：出发国家；D：到达国家；E：二选一；
        String curCode = StringUtils.trimToEmpty(cat015.getCurcode()); // 售票国家只可以规定

        if (StringUtils.isNotBlank(curCtry)) {
            char curCtryCode = curCtry.charAt(0);
            switch (curCtryCode) {
                case 'O':
                    localeMessageSource.newCat015MsgLineL3(cat015Msge, "cat015.temp.cur.ctry.o", new Object[]{origCtry});
                    break;
                case 'D':
                    localeMessageSource.newCat015MsgLineL3(cat015Msge, "cat015.temp.cur.ctry.d", new Object[]{destCtry});
                    break;
                case 'E':
                    localeMessageSource.newCat015MsgLineL3(cat015Msge, "cat015.temp.cur.ctry.e", new Object[]{origCtry, destCtry});
                    break;
                default:
                    break;
            }
        }

        if (StringUtils.isNotBlank(curCode)) {
            localeMessageSource.newCat015MsgLineL3(cat015Msge, "cat015.temp.cur.code", new Object[]{curCode});
        }

        String tkisMail = StringUtils.trimToEmpty(cat015.getTkismail()); // 邮寄出票限制：空：不适用；Y：可以；N：不可以；R：可申请；
        String tkisPta = StringUtils.trimToEmpty(cat015.getTkispta()); // 异地付款出票限制：空：不适用；Y：可以；N：不可以；R：可申请；
        String tkisMech = StringUtils.trimToEmpty(cat015.getTkismech()); // 机器出票限制：空：不适用；Y：可以；N：不可以；R：可申请；
        String tkisSelf = StringUtils.trimToEmpty(cat015.getTkisself()); // 自助出票限制：空：不适用；Y：可以；N：不可以；R：可申请；
        String tkisPtat = StringUtils.trimToEmpty(cat015.getTkisptat()); // 异地取票出票限制：空：不适用；Y：可以；N：不可以；R：可申请；
        String tkisAtm = StringUtils.trimToEmpty(cat015.getTkisatm()); // 自助机器出票限制：空：不适用；Y：可以；N：不可以；R：可申请；
        String tkisStp = StringUtils.trimToEmpty(cat015.getTkisstp()); // 卫星机器出票限制：空：不适用；Y：可以；N：不可以；R：可申请；
        String tkisSato = StringUtils.trimToEmpty(cat015.getTkissato()); // SATO/CATO出票限制：空：不适用；Y：可以；N：不可以；R：可申请；
        String tkisEtkt = StringUtils.trimToEmpty(cat015.getTkisetkt()); // 电子出票限制：空：不适用；Y：可以；N：不可以；R：可申请；
        String tkisRsvd = StringUtils.trimToEmpty(cat015.getTkisrsvd()); // 存根出票限制：空：不适用；Y：可以；N：不可以；R：可申请；

        String tkisYs = StringUtils.EMPTY;
        String tkisNs = StringUtils.EMPTY;
        String tkisRs = StringUtils.EMPTY;
        if (StringUtils.isNotBlank(tkisMail)) {
            char tkisMailCode = tkisMail.charAt(0);
            String mailKey = "cat015.temp.tkis.mail";
            if (tkisMailCode == 'Y') {
                tkisYs = tkisYs.concat(DispExtConstants.SPACE).concat(localeMessageSource.newCat015Msg(mailKey));
            } else if (tkisMailCode == 'R') {
                tkisRs = tkisRs.concat(DispExtConstants.SPACE).concat(localeMessageSource.newCat015Msg(mailKey));
            } else if (tkisMailCode == 'N') {
                tkisNs = tkisNs.concat(DispExtConstants.SPACE).concat(localeMessageSource.newCat015Msg(mailKey));
            }
        }
        if (StringUtils.isNotBlank(tkisPta)) {
            char tkisPtaCode = tkisPta.charAt(0);
            String ptaKey = "cat015.temp.tkis.pta";
            if (tkisPtaCode == 'Y') {
                tkisYs = tkisYs.concat(DispExtConstants.SPACE).concat(localeMessageSource.newCat015Msg(ptaKey));
            } else if (tkisPtaCode == 'R') {
                tkisRs = tkisRs.concat(DispExtConstants.SPACE).concat(localeMessageSource.newCat015Msg(ptaKey));
            } else if (tkisPtaCode == 'N') {
                tkisNs = tkisNs.concat(DispExtConstants.SPACE).concat(localeMessageSource.newCat015Msg(ptaKey));
            }
        }
        if (StringUtils.isNotBlank(tkisMech)) {
            char tkisMechCode = tkisMech.charAt(0);
            String mechKey = "cat015.temp.tkis.mech";
            if (tkisMechCode == 'Y') {
                tkisYs = tkisYs.concat(DispExtConstants.SPACE).concat(localeMessageSource.newCat015Msg(mechKey));
            } else if (tkisMechCode == 'R') {
                tkisRs = tkisRs.concat(DispExtConstants.SPACE).concat(localeMessageSource.newCat015Msg(mechKey));
            } else if (tkisMechCode == 'N') {
                tkisNs = tkisNs.concat(DispExtConstants.SPACE).concat(localeMessageSource.newCat015Msg(mechKey));
            }
        }
        if (StringUtils.isNotBlank(tkisSelf)) {
            char tkisSelfCode = tkisSelf.charAt(0);
            String selfKey = "cat015.temp.tkis.self";
            if (tkisSelfCode == 'Y') {
                tkisYs = tkisYs.concat(DispExtConstants.SPACE).concat(localeMessageSource.newCat015Msg(selfKey));
            } else if (tkisSelfCode == 'R') {
                tkisRs = tkisRs.concat(DispExtConstants.SPACE).concat(localeMessageSource.newCat015Msg(selfKey));
            } else if (tkisSelfCode == 'N') {
                tkisNs = tkisNs.concat(DispExtConstants.SPACE).concat(localeMessageSource.newCat015Msg(selfKey));
            }
        }
        if (StringUtils.isNotBlank(tkisPtat)) {
            char tkisPtatCode = tkisPtat.charAt(0);
            String ptatKey = "cat015.temp.tkis.ptat";
            if (tkisPtatCode == 'Y') {
                tkisYs = tkisYs.concat(DispExtConstants.SPACE).concat(localeMessageSource.newCat015Msg(ptatKey));
            } else if (tkisPtatCode == 'R') {
                tkisRs = tkisRs.concat(DispExtConstants.SPACE).concat(localeMessageSource.newCat015Msg(ptatKey));
            } else if (tkisPtatCode == 'N') {
                tkisNs = tkisNs.concat(DispExtConstants.SPACE).concat(localeMessageSource.newCat015Msg(ptatKey));
            }
        }
        if (StringUtils.isNotBlank(tkisAtm)) {
            char tkisAtmCode = tkisAtm.charAt(0);
            String atmKey = "cat015.temp.tkis.atm";
            if (tkisAtmCode == 'Y') {
                tkisYs = tkisYs.concat(DispExtConstants.SPACE).concat(localeMessageSource.newCat015Msg(atmKey));
            } else if (tkisAtmCode == 'R') {
                tkisRs = tkisRs.concat(DispExtConstants.SPACE).concat(localeMessageSource.newCat015Msg(atmKey));
            } else if (tkisAtmCode == 'N') {
                tkisNs = tkisNs.concat(DispExtConstants.SPACE).concat(localeMessageSource.newCat015Msg(atmKey));
            }
        }
        if (StringUtils.isNotBlank(tkisStp)) {
            char tkisStpCode = tkisStp.charAt(0);
            String stpKey = "cat015.temp.tkis.stp";
            if (tkisStpCode == 'Y') {
                tkisYs = tkisYs.concat(DispExtConstants.SPACE).concat(localeMessageSource.newCat015Msg(stpKey));
            } else if (tkisStpCode == 'R') {
                tkisRs = tkisRs.concat(DispExtConstants.SPACE).concat(localeMessageSource.newCat015Msg(stpKey));
            } else if (tkisStpCode == 'N') {
                tkisNs = tkisNs.concat(DispExtConstants.SPACE).concat(localeMessageSource.newCat015Msg(stpKey));
            }
        }
        if (StringUtils.isNotBlank(tkisSato)) {
            char tkisSatoCode = tkisSato.charAt(0);
            String satoKey = "cat015.temp.tkis.sato";
            if (tkisSatoCode == 'Y') {
                tkisYs = tkisYs.concat(DispExtConstants.SPACE).concat(localeMessageSource.newCat015Msg(satoKey));
            } else if (tkisSatoCode == 'R') {
                tkisRs = tkisRs.concat(DispExtConstants.SPACE).concat(localeMessageSource.newCat015Msg(satoKey));
            } else if (tkisSatoCode == 'N') {
                tkisNs = tkisNs.concat(DispExtConstants.SPACE).concat(localeMessageSource.newCat015Msg(satoKey));
            }
        }
        if (StringUtils.isNotBlank(tkisEtkt)) {
            char tkisEtktCode = tkisEtkt.charAt(0);
            String etktKey = "cat015.temp.tkis.etkt";
            if (tkisEtktCode == 'Y') {
                tkisYs = tkisYs.concat(DispExtConstants.SPACE).concat(localeMessageSource.newCat015Msg(etktKey));
            } else if (tkisEtktCode == 'R') {
                tkisRs = tkisRs.concat(DispExtConstants.SPACE).concat(localeMessageSource.newCat015Msg(etktKey));
            } else if (tkisEtktCode == 'N') {
                tkisNs = tkisNs.concat(DispExtConstants.SPACE).concat(localeMessageSource.newCat015Msg(etktKey));
            }
        }
        if (StringUtils.isNotBlank(tkisRsvd)) {
            char tkisRsvdCode = tkisRsvd.charAt(0);
            String rsvdKey = "cat015.temp.tkis.rsvd";
            if (tkisRsvdCode == 'Y') {
                tkisYs = tkisYs.concat(DispExtConstants.SPACE).concat(localeMessageSource.newCat015Msg(rsvdKey));
            } else if (tkisRsvdCode == 'R') {
                tkisRs = tkisRs.concat(DispExtConstants.SPACE).concat(localeMessageSource.newCat015Msg(rsvdKey));
            } else if (tkisRsvdCode == 'N') {
                tkisNs = tkisNs.concat(DispExtConstants.SPACE).concat(localeMessageSource.newCat015Msg(rsvdKey));
            }
        }
        if (StringUtils.isNotBlank(tkisYs)) {
            localeMessageSource.newCat015MsgLineL3(cat015Msge, "cat015.temp.tkis.y", new Object[]{tkisYs.trim()});
        }
        if (StringUtils.isNotBlank(tkisNs)) {
            localeMessageSource.newCat015MsgLineL3(cat015Msge, "cat015.temp.tkis.n", new Object[]{tkisNs.trim()});
        }
        if (StringUtils.isNotBlank(tkisRs)) {
            localeMessageSource.newCat015MsgLineL3(cat015Msge, "cat015.temp.tkis.r", new Object[]{tkisRs.trim()});
        }

        String siti = StringUtils.trimToEmpty(cat015.getSiti()); // 国内销售和国内出票限制，相对于出发国：空：不适用；Y：可以；N：不可以；R：可申请；
        String soto = StringUtils.trimToEmpty(cat015.getSoto()); // 国外销售和国外出票限制，相对于出发国：空：不适用；Y：可以；N：不可以；R：可申请；
        String sito = StringUtils.trimToEmpty(cat015.getSito()); // 国内销售和国外出票限制，相对于出发国：空：不适用；Y：可以；N：不可以；R：可申请；
        String soti = StringUtils.trimToEmpty(cat015.getSoti()); // 国外销售和国内出票限制，相对于出发国：空：不适用；Y：可以；N：不可以；R：可申请；
        String reserved = StringUtils.isBlank(reserve) ? StringUtils.trimToEmpty(cat015.getReserved()) : reserve; // 存根限制

        List<String> stNs = new ArrayList<>();
        List<String> stYs = new ArrayList<>();
        List<String> stRs = new ArrayList<>();
        if (StringUtils.isNotBlank(siti)) {
            char sitiCode = siti.charAt(0);
            String sitiKey = "cat015.temp.tkis.st.siti";
            if (sitiCode == 'Y') {
                stYs.add(localeMessageSource.newCat015Msg(sitiKey, new Object[]{origCtry}));
            } else if (sitiCode == 'N') {
                stNs.add(localeMessageSource.newCat015Msg(sitiKey, new Object[]{origCtry}));
            } else if (sitiCode == 'R') {
                stRs.add(localeMessageSource.newCat015Msg(sitiKey, new Object[]{origCtry}));
            }
        }
        if (StringUtils.isNotBlank(soto)) {
            char sotoCode = soto.charAt(0);
            String sotoKey = "cat015.temp.tkis.st.soto";
            if (sotoCode == 'Y') {
                stYs.add(localeMessageSource.newCat015Msg(sotoKey, new Object[]{origCtry}));
            } else if (sotoCode == 'N') {
                stNs.add(localeMessageSource.newCat015Msg(sotoKey, new Object[]{origCtry}));
            } else if (sotoCode == 'R') {
                stRs.add(localeMessageSource.newCat015Msg(sotoKey, new Object[]{origCtry}));
            }
        }
        if (StringUtils.isNotBlank(sito)) {
            char sitoCode = sito.charAt(0);
            String sitoKey = "cat015.temp.tkis.st.sito";
            if (sitoCode == 'Y') {
                stYs.add(localeMessageSource.newCat015Msg(sitoKey, new Object[]{origCtry}));
            } else if (sitoCode == 'N') {
                stNs.add(localeMessageSource.newCat015Msg(sitoKey, new Object[]{origCtry}));
            } else if (sitoCode == 'R') {
                stRs.add(localeMessageSource.newCat015Msg(sitoKey, new Object[]{origCtry}));
            }
        }
        if (StringUtils.isNotBlank(soti)) {
            char sotiCode = soti.charAt(0);
            String sotiKey = "cat015.temp.tkis.st.soti";
            if (sotiCode == 'Y') {
                stYs.add(localeMessageSource.newCat015Msg(sotiKey, new Object[]{origCtry}));
            } else if (sotiCode == 'N') {
                stNs.add(localeMessageSource.newCat015Msg(sotiKey, new Object[]{origCtry}));
            } else if (sotiCode == 'R') {
                stRs.add(localeMessageSource.newCat015Msg(sotiKey, new Object[]{origCtry}));
            }
        }
        if (CollectionUtils.isNotEmpty(stYs)) {
            localeMessageSource.newCat015MsgLineL3(cat015Msge, "cat015.temp.tkis.st.y");
            for (String stY : stYs) {
                localeMessageSource.newCat015MsgLineL4(stY, cat015Msge);
            }
        }
        if (CollectionUtils.isNotEmpty(stNs)) {
            localeMessageSource.newCat015MsgLineL3(cat015Msge, "cat015.temp.tkis.st.n");
            for (String stN : stNs) {
                localeMessageSource.newCat015MsgLineL4(stN, cat015Msge);
            }
        }
        if (CollectionUtils.isNotEmpty(stRs)) {
            localeMessageSource.newCat015MsgLineL3(cat015Msge, "cat015.temp.tkis.st.r");
            for (String stR : stRs) {
                localeMessageSource.newCat015MsgLineL4(stR, cat015Msge);
            }
        }
        if (StringUtils.isNotBlank(reserved)) {
            localeMessageSource.newCat015MsgLineL3(cat015Msge, "cat015.temp.tkis.st.rsvd");
        }

        String famgrp = StringUtils.trimToEmpty(cat015.getFamgrp()); // 家庭团体票同时发布限制；X：必须；空：不必须；

        if (StringUtils.isNotBlank(famgrp) && famgrp.charAt(0) == 'X') {
            localeMessageSource.newCat015MsgLineL3(cat015Msge, "cat015.temp.famgrp");
        }

        String extens = StringUtils.trimToEmpty(cat015.getExtens()); // 延长有效期限制；X：可以；N：不可以；空：没有限制；

        if (StringUtils.isNotBlank(extens)) {
            char extensCode = extens.charAt(0);
            switch (extensCode) {
                case 'X':
                    localeMessageSource.newCat015MsgLineL3(cat015Msge, "cat015.temp.extens.x");
                    break;
                case 'N':
                    localeMessageSource.newCat015MsgLineL3(cat015Msge, "cat015.temp.extens.n");
                    break;
                default:
                    break;
            }
        }

        List<TTbl996> tbl996s = tbl900.getTbl996s();

        if (CollectionUtils.isNotEmpty(tbl996s)) {
            localeMessageSource.newCat015MsgLineL2(cat015Msge, "cat015.temp.tbl996.tilte");
            Collections.sort(tbl996s, new Comparator<TTbl996>() {
                @Override
                public int compare(TTbl996 o1, TTbl996 o2) {
                    return o1.getLineNo().compareTo(o2.getLineNo());
                }
            });
            for (TTbl996 tbl996 : tbl996s) {
                localeMessageSource.newCat015MsgLineL3(tbl996.getText(), cat015Msge);
            }
        }

        List<TCat015Segs> cat015Segs = cat015.getSegs();
        boolean cat015SegTitle = true;

        for (TCat015Segs cat015Seg : cat015Segs) {
            String appl = StringUtils.trimToEmpty(cat015Seg.getAppl()); // 逻辑正反；Y：只可以；N：不可以；
            String loc1Code = StringUtils.trimToEmpty(cat015Seg.getLoc1code()); // 
            String loc2Code = StringUtils.trimToEmpty(cat015Seg.getLoc2code());
            String locType = StringUtils.trimToEmpty(cat015Seg.getLoctype()); // 地理范围类型：空/A：地区；C：城市；H：国内IATA旅行社编号；I：IATA旅行社编号
            // N：国家；P：机场；S：国家/地区；T：旅行社；U：国内旅行社编号；V：CRS/CXR部门编号
            // X：部门/标志；Z：区域
            if (StringUtils.isNotBlank(appl)) {
                char applCode = appl.charAt(0);
                String applKey = null;
                switch (applCode) {
                    case 'Y':
                        applKey = "cat015.temp.loc.appl.y";
                        break;
                    case 'N':
                        applKey = "cat015.temp.loc.appl.n";
                        break;
                    default:
                        break;
                }
                if (StringUtils.isNotBlank(applKey)) {
                    String locTypeContext = localeMessageSource.newCat015Msg("cat015.temp.loc.type.a");
                    if (StringUtils.isNotBlank(locType)) {
                        char locTypeCode = locType.charAt(0);
                        switch (locTypeCode) {
                            case 'A':
                                locTypeContext = localeMessageSource.newCat015Msg("cat015.temp.loc.type.a");
                                break;
                            case 'C':
                                locTypeContext = localeMessageSource.newCat015Msg("cat015.temp.loc.type.c");
                                break;
                            case 'H':
                                locTypeContext = localeMessageSource.newCat015Msg("cat015.temp.loc.type.h");
                                break;
                            case 'I':
                                locTypeContext = localeMessageSource.newCat015Msg("cat015.temp.loc.type.i");
                                break;
                            case 'N':
                                locTypeContext = localeMessageSource.newCat015Msg("cat015.temp.loc.type.n");
                                break;
                            case 'P':
                                locTypeContext = localeMessageSource.newCat015Msg("cat015.temp.loc.type.p");
                                break;
                            case 'S':
                                locTypeContext = localeMessageSource.newCat015Msg("cat015.temp.loc.type.s");
                                break;
                            case 'T':
                                locTypeContext = localeMessageSource.newCat015Msg("cat015.temp.loc.type.t");
                                break;
                            case 'U':
                                locTypeContext = localeMessageSource.newCat015Msg("cat015.temp.loc.type.u");
                                break;
                            case 'V':
                                locTypeContext = localeMessageSource.newCat015Msg("cat015.temp.loc.type.v");
                                break;
                            case 'X':
                                locTypeContext = localeMessageSource.newCat015Msg("cat015.temp.loc.type.x");
                                break;
                            case 'Z':
                                locTypeContext = localeMessageSource.newCat015Msg("cat015.temp.loc.type.z");
                                break;
                            default:
                                break;
                        }
                        if (StringUtils.isNotBlank(locTypeContext) && (StringUtils.isNotBlank(loc1Code) || StringUtils.isNotBlank(loc2Code))) {
                            if (cat015SegTitle) {
                                localeMessageSource.newCat015MsgLineL2(cat015Msge, "cat015.temp.loc.title");
                                cat015SegTitle = false;
                            }
                            localeMessageSource.newCat015MsgLineL3(cat015Msge, applKey, new Object[]{locTypeContext, loc1Code, loc2Code});
                        }
                    }
                }
            }

        }

        return cat015Msge.toString();
    }

	@Override
	public void dispExtract(TCat015 cat, DispCat015TBL900 tbl900, DispFareQueryOutput dispFareQueryOutput) {
		// TODO Auto-generated method stub
		
	}

}

class LocaleMessageSource {

    private MessageSource messageSource;
    private Locale locale;

    public LocaleMessageSource(MessageSource messageSource, Locale locale) {
        this.messageSource = messageSource;
        this.locale = locale;
    }

    public void newCat015MsgLineL0(String message, StringBuilder cat015Msge) {
        cat015Msge.append(message).append(DispExtConstants.RN);
    }

    public void newCat015MsgLineL0(StringBuilder cat015Msge, String key, Object[] values) {
        cat015Msge.append(newCat015Msg(key, values)).append(DispExtConstants.RN);
    }

    public void newCat015MsgLineL0(StringBuilder cat015Msge, String key) {
        cat015Msge.append(newCat015Msg(key)).append(DispExtConstants.RN);
    }


    public void newCat015MsgLineL1(String message, StringBuilder cat015Msge) {
        cat015Msge.append(DispExtConstants.SPACE_LEVEL1).append(message).append(DispExtConstants.RN);
    }

    public void newCat015MsgLineL1(StringBuilder cat015Msge, String key, Object[] values) {
        cat015Msge.append(DispExtConstants.SPACE_LEVEL1).append(newCat015Msg(key, values)).append(DispExtConstants.RN);
    }

    public void newCat015MsgLineL1(StringBuilder cat015Msge, String key) {
        cat015Msge.append(DispExtConstants.SPACE_LEVEL1).append(newCat015Msg(key)).append(DispExtConstants.RN);
    }


    public void newCat015MsgLineL2(String message, StringBuilder cat015Msge) {
        cat015Msge.append(DispExtConstants.SPACE_LEVEL2).append(message).append(DispExtConstants.RN);
    }

    public void newCat015MsgLineL2(StringBuilder cat015Msge, String key, Object[] values) {
        cat015Msge.append(DispExtConstants.SPACE_LEVEL2).append(newCat015Msg(key, values)).append(DispExtConstants.RN);
    }

    public void newCat015MsgLineL2(StringBuilder cat015Msge, String key) {
        cat015Msge.append(DispExtConstants.SPACE_LEVEL2).append(newCat015Msg(key)).append(DispExtConstants.RN);
    }

    public void newCat015MsgLineL3(String message, StringBuilder cat015Msge) {
        cat015Msge.append(DispExtConstants.SPACE_LEVEL3).append(message).append(DispExtConstants.RN);
    }

    public void newCat015MsgLineL3(StringBuilder cat015Msge, String key, Object[] values) {
        cat015Msge.append(DispExtConstants.SPACE_LEVEL3).append(newCat015Msg(key, values)).append(DispExtConstants.RN);
    }

    public void newCat015MsgLineL3(StringBuilder cat015Msge, String key) {
        cat015Msge.append(DispExtConstants.SPACE_LEVEL3).append(newCat015Msg(key)).append(DispExtConstants.RN);
    }

    public void newCat015MsgLineL4(String message, StringBuilder cat015Msge) {
        cat015Msge.append(DispExtConstants.SPACE_LEVEL4).append(message).append(DispExtConstants.RN);
    }

    public void newCat015MsgLineL4(StringBuilder cat015Msge, String key, Object[] values) {
        cat015Msge.append(DispExtConstants.SPACE_LEVEL4).append(newCat015Msg(key, values)).append(DispExtConstants.RN);
    }

    public void newCat015MsgLineL4(StringBuilder cat015Msge, String key) {
        cat015Msge.append(DispExtConstants.SPACE_LEVEL4).append(newCat015Msg(key)).append(DispExtConstants.RN);
    }

    public String newCat015Msg(String key, Object[] values) {
        return messageSource.getMessage(key, values, locale);
    }

    public String newCat015Msg(String key) {
        return messageSource.getMessage(key, null, locale);
    }