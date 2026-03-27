package com.duoduo.jxc.dto.settings;

import lombok.Data;

@Data
public class SystemSettingsDTO {

    private Boolean enableSizeColor;

    private String systemName;
    private String companyName;
    private String companyPhone;
    private String companyAddress;

    private String creatorSelectable;
    private String autoAudit;
    private String enableBatch;
    private String enableShelfLife;
    private String enableBarcode;
    private String enableInventory;
    private String salesCarryAmount;
    private String purchaseCarryAmount;
    private String forbidZeroStockOut;
    private String hideApprover;
    private String allowEditPrice;
    private String billingSalesman;
    private String warnLowPrice;
    private String salesTaxRate;
    private Boolean enableSalesTax;
    private Boolean salesIncludeTax;
    private String purchaseTaxRate;
    private Boolean enablePurchaseTax;
    private Boolean purchaseIncludeTax;

    private String priceDecimals;
    private String qtyDecimals;
    private String pieceRound;
    private String cloudDriveUrl;
    private String printerSelectable;
    private String billingShowImage;
    private String billingScanAdd;

    private Integer inventoryWarningDefault;
    private String enableQuickProductDropdown;
    private String allowDuplicateBarcode;
    private String allowDuplicateNameSpec;
    private String allowNewProductOnBilling;

    private String defaultPaper;
    private String reportBatchPrint;
    private String continuousPrint;

    private String printClothesWholesaleLayout;
    private String printTextileStyleLayout;

    private String remotePrintAccount;
    private String remoteCashAccount;

    private String multiDeliveryCalc;
    private String timezone;

    private String retailPermission;
    private String pointsRatio;
    private Boolean enablePoints;
    private String autoPrintReceipt;
    private String defaultCustomer;
    private String defaultAccount;

    private String chartDays;
    private String noticeInfo;
}
