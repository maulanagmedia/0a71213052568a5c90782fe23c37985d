package gmedia.net.id.pspreseller.Utils;

/**
 * Created by Shinmaul on 1/25/2018.
 */

public class ServerURL {

    //private static final String baseURL = "http://192.168.12.188/psp/apiresv1/";
    //private static final String baseURL = "http://119.2.53.122/mobilesalesforce/apiresv1/";
    private static final String baseURL = "http://putmasaripratama.co.id/mobilesalesforce/apiresv1/";
    //private static final String baseURL = "http://api.putmasaripratama.co.id/apiresv1/";

    public static final String login = baseURL + "auth/login/";
    public static final String register = baseURL + "Auth/register/";
    public static final String getOTP = baseURL + "Auth/kirim_otp/";
    public static final String resetPassword = baseURL + "Auth/reset_password/";
    public static final String changePassword = baseURL + "Auth/change_password/";
    public static final String getPromosi = baseURL + "promosi/get_promosi/";
    public static final String uploadFileURL = baseURL + "upload/upload_file/";

    public static final String getChat = baseURL + "chat/get_chat/";
    public static final String saveChat = baseURL + "chat/insert/";
    public static final String deleteChat = baseURL + "chat/delele_chat/";

    public static final String getProfile = baseURL + "profile/view_profile/";
    public static final String getNews = baseURL + "Promosi/get_news";
    public static final String getNomor = baseURL + "profile/master_nomor/";
    public static final String getDenom = baseURL + "profile/master_harga/";
    public static final String ubahPin = baseURL + "profile/ubah_pin/";
    public static final String priceList = baseURL + "profile/pricelist/";
    public static final String getPSPInformastion = baseURL + "Profile/view_customer_service/";
    public static final String getTotalDeposit = baseURL + "profile/total_deposite/";
    public static final String saveInfoStok = baseURL + "Profile/simpan_stok/";
    public static final String getInfoStok = baseURL + "Profile/view_stok/";
    public static final String getSavedPin = baseURL + "Profile/view_pin/";
    public static final String savePinFlag = baseURL + "Profile/simpan_pin/";
    public static final String getPhonebook = baseURL + "Profile/view_phonebook/";

    public static final String beliMkios = baseURL + "Mkios/beli_mkios/";
    public static final String beliBulk = baseURL + "Mkios/beli_bulk/";
    public static final String beliTcash = baseURL + "Mkios/beli_tcash/";
    public static final String beliSaldoTunai= baseURL + "Mkios/beli_deposit/";
    public static final String getHarga = baseURL + "mkios/get_harga/";
    public static final String viewTransaksi = baseURL + "mkios/view_transaksi/";
    public static final String viewHistory = baseURL + "mkios/view_history/";
    public static final String topUpDeposit = baseURL + "deposite/topup/";
    public static final String viewDeposit = baseURL + "deposite/view_deposite/";
    public static final String viewHistoryDeposit = baseURL + "Deposite/view_history/";
    public static final String getBarangDS = baseURL + "Direct_Sale/view_barang/";
    public static final String saveReplyDS = baseURL + "Direct_Sale/save_reply/";
    public static final String saveOrderDS = baseURL + "Direct_Sale/direct_order/";
    public static final String saveDSPerdana = baseURL + "Direct_Sale/jual_perdana/";
    public static final String checkSaldo = baseURL + "Mkios/cek_saldo/";
    public static final String getLatestVersion = baseURL + "Version/";
    public static final String getBukuPintar = baseURL + "Buku/";
    public static final String beliPerdana = baseURL + "Perdana/pre_order/";
    public static final String getCCID = baseURL + "Direct_Sale/scan_perdana/";

    //public static final String uploadFileURL = "http://192.168.12.147/psp/testupload/upload.php";
}
