package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.constant.QrCodeConstant;
import com.duoduo.jxc.entity.QrCode;
import com.duoduo.jxc.service.QrCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/mes/qrcode")
@RequiredArgsConstructor
public class QrCodeController {

    private final QrCodeService qrCodeService;

    @Log(title = "二维码管理", action = "生成扎包二维码")
    @PostMapping("/bundle/{bundleId}")
    @PreAuthorize("@perm.has('mes:scan:add')")
    public Result<QrCode> generateBundleQrCode(@PathVariable Long bundleId,
                                                @RequestParam String bundleNo) {
        return Result.success(qrCodeService.generateBundleQrCode(bundleId, bundleNo));
    }

    @Log(title = "二维码管理", action = "获取二维码详情")
    @GetMapping("/{qrCodeNo}")
    @PreAuthorize("@perm.has('mes:scan:view')")
    public Result<QrCode> getByQrCodeNo(@PathVariable String qrCodeNo) {
        return Result.success(qrCodeService.getByQrCodeNo(qrCodeNo));
    }

    @Log(title = "二维码管理", action = "获取二维码图片")
    @GetMapping(value = "/image/{qrCodeNo}", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getQrImage(@PathVariable String qrCodeNo,
                             @RequestParam(defaultValue = "300") int size) throws Exception {
        QrCode qrCode = qrCodeService.getByQrCodeNo(qrCodeNo);
        if (qrCode == null) {
            return new byte[0];
        }
        BufferedImage image = qrCodeService.generateQrImage(qrCode.getQrContent(), size);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, QrCodeConstant.QR_IMAGE_FORMAT, baos);
        return baos.toByteArray();
    }

    @Log(title = "二维码管理", action = "验证二维码")
    @PostMapping("/verify")
    public Result<Boolean> verify(@RequestParam String qrCodeNo,
                                  @RequestParam String signature) {
        QrCode qrCode = qrCodeService.getByQrCodeNo(qrCodeNo);
        if (qrCode == null) {
            return Result.success(false);
        }
        return Result.success(qrCodeService.verifySignature(qrCode.getQrContent(), signature));
    }
}
