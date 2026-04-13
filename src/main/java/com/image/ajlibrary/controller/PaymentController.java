package com.image.ajlibrary.controller;

import com.image.ajlibrary.entity.BorrowRecord;
import com.image.ajlibrary.repository.BorrowRecordRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "http://localhost:3000")
public class PaymentController {

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpaySecret;

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestParam Long recordId) {
        try {
            BorrowRecord record = borrowRecordRepository.findById(recordId)
                    .orElseThrow(() -> new RuntimeException("Record not found"));

            if (record.getFine() <= 0) {
                return ResponseEntity.badRequest().body(Map.of("message", "No fine pending for this record"));
            }

            if (Boolean.TRUE.equals(record.getFinePaid())) {
                return ResponseEntity.badRequest().body(Map.of("message", "Fine already paid"));
            }

            RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpaySecret);
            JSONObject orderRequest = new JSONObject();
            // Amount in paise (multiply by 100)
            orderRequest.put("amount", (int) (record.getFine() * 100));
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "txn_" + recordId);

            Order order = razorpay.orders.create(orderRequest);

            return ResponseEntity
                    .ok(Map.of("orderId", order.get("id").toString(), "amount", order.get("amount").toString()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "Error creating Razorpay Order", "error", e.getMessage()));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> data, @RequestParam Long recordId) {
        try {
            String razorpayOrderId = data.get("razorpay_order_id");
            String razorpayPaymentId = data.get("razorpay_payment_id");
            String razorpaySignature = data.get("razorpay_signature");

            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", razorpayOrderId);
            options.put("razorpay_payment_id", razorpayPaymentId);
            options.put("razorpay_signature", razorpaySignature);

            boolean isValid = Utils.verifyPaymentSignature(options, razorpaySecret);

            if (isValid) {
                BorrowRecord record = borrowRecordRepository.findById(recordId)
                        .orElseThrow(() -> new RuntimeException("Record not found"));

                record.setFinePaid(true);
                borrowRecordRepository.save(record);
                return ResponseEntity.ok(Map.of("message", "Payment Successful & Verified", "success", true));
            } else {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Invalid Payment Signature", "success", false));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("message", "Payment verification error"));
        }
    }
}
