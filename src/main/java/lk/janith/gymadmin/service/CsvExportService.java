package lk.janith.gymadmin.service;

import jakarta.servlet.http.HttpServletResponse;
import lk.janith.gymadmin.entity.AccessLog;
import lk.janith.gymadmin.entity.Member;
import lk.janith.gymadmin.entity.MemberStatus;
import lk.janith.gymadmin.entity.Payment;
import lk.janith.gymadmin.repository.AccessLogRepository;
import lk.janith.gymadmin.repository.MemberRepository;
import lk.janith.gymadmin.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CsvExportService {

    private final MemberRepository memberRepository;
    private final PaymentRepository paymentRepository;
    private final AccessLogRepository accessLogRepository;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void exportAllMembers(HttpServletResponse response) throws IOException {
        List<Member> members = memberRepository.findAll();

        PrintWriter writer = prepareCsvResponse(response, "all-members.csv");

        writeRow(writer,
                "ID",
                "Member Code",
                "Full Name",
                "Phone",
                "NIC",
                "Gender",
                "Age",
                "Joined Date",
                "Status",
                "Device User ID"
        );

        for (Member member : members) {
            writeMemberRow(writer, member);
        }

        writer.flush();
    }

    public void exportActiveMembers(HttpServletResponse response) throws IOException {
        List<Member> members = memberRepository.findByStatus(MemberStatus.ACTIVE);

        PrintWriter writer = prepareCsvResponse(response, "active-members.csv");

        writeRow(writer,
                "ID",
                "Member Code",
                "Full Name",
                "Phone",
                "NIC",
                "Gender",
                "Age",
                "Joined Date",
                "Status",
                "Device User ID"
        );

        for (Member member : members) {
            writeMemberRow(writer, member);
        }

        writer.flush();
    }

    public void exportExpiredMembers(HttpServletResponse response) throws IOException {
        List<Member> members = memberRepository.findByStatus(MemberStatus.EXPIRED);

        PrintWriter writer = prepareCsvResponse(response, "expired-members.csv");

        writeRow(writer,
                "ID",
                "Member Code",
                "Full Name",
                "Phone",
                "NIC",
                "Gender",
                "Age",
                "Joined Date",
                "Status",
                "Device User ID"
        );

        for (Member member : members) {
            writeMemberRow(writer, member);
        }

        writer.flush();
    }

    public void exportPayments(HttpServletResponse response) throws IOException {
        List<Payment> payments = paymentRepository.findAll();

        PrintWriter writer = prepareCsvResponse(response, "payments.csv");

        writeRow(writer,
                "ID",
                "Member Code",
                "Member Name",
                "Phone",
                "Plan",
                "Amount",
                "Payment Method",
                "Payment Date",
                "Start Date",
                "Expiry Date",
                "Status",
                "Note"
        );

        for (Payment payment : payments) {
            writeRow(writer,
                    value(payment.getId()),
                    payment.getMember() != null ? value(payment.getMember().getMemberCode()) : "",
                    payment.getMember() != null ? value(payment.getMember().getFullName()) : "",
                    payment.getMember() != null ? value(payment.getMember().getPhone()) : "",
                    payment.getMembershipPlan() != null ? value(payment.getMembershipPlan().getPlanName()) : "",
                    value(payment.getAmount()),
                    value(payment.getPaymentMethod()),
                    formatDate(payment.getPaymentDate()),
                    formatDate(payment.getStartDate()),
                    formatDate(payment.getExpiryDate()),
                    value(payment.getStatus()),
                    value(payment.getNote())
            );
        }

        writer.flush();
    }

    public void exportAccessLogs(HttpServletResponse response) throws IOException {
        List<AccessLog> logs = accessLogRepository.findAllByOrderByAccessTimeDesc();

        PrintWriter writer = prepareCsvResponse(response, "access-logs.csv");

        writeRow(writer,
                "ID",
                "Entered Member Code",
                "Member Code",
                "Member Name",
                "Phone",
                "Access Status",
                "Reason",
                "Access Time"
        );

        for (AccessLog log : logs) {
            writeRow(writer,
                    value(log.getId()),
                    value(log.getEnteredMemberCode()),
                    log.getMember() != null ? value(log.getMember().getMemberCode()) : "",
                    log.getMember() != null ? value(log.getMember().getFullName()) : "",
                    log.getMember() != null ? value(log.getMember().getPhone()) : "",
                    value(log.getAccessStatus()),
                    value(log.getReason()),
                    formatDateTime(log.getAccessTime())
            );
        }

        writer.flush();
    }

    private void writeMemberRow(PrintWriter writer, Member member) {
        writeRow(writer,
                value(member.getId()),
                value(member.getMemberCode()),
                value(member.getFullName()),
                value(member.getPhone()),
                value(member.getNic()),
                value(member.getGender()),
                value(member.getAge()),
                formatDate(member.getJoinedDate()),
                value(member.getStatus()),
                value(member.getDeviceUserId())
        );
    }

    private PrintWriter prepareCsvResponse(HttpServletResponse response, String fileName) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        PrintWriter writer = response.getWriter();

        // UTF-8 BOM helps Excel open the CSV correctly
        writer.write('\ufeff');

        return writer;
    }

    private void writeRow(PrintWriter writer, String... columns) {
        String row = Arrays.stream(columns)
                .map(this::escapeCsv)
                .collect(Collectors.joining(","));

        writer.println(row);
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }

        String safeValue = value.replace("\"", "\"\"");

        if (safeValue.contains(",") || safeValue.contains("\"") || safeValue.contains("\n") || safeValue.contains("\r")) {
            return "\"" + safeValue + "\"";
        }

        return safeValue;
    }

    private String value(Object object) {
        return object == null ? "" : String.valueOf(object);
    }

    private String formatDate(LocalDate date) {
        return date == null ? "" : date.format(dateFormatter);
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime == null ? "" : dateTime.format(dateTimeFormatter);
    }
}