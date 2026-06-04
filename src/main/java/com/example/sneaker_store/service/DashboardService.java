package com.example.sneaker_store.service;

import com.example.sneaker_store.dto.response.DashboardResponse;
import com.example.sneaker_store.dto.response.FavouriteResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public DashboardResponse getDashboardStatistic() {
        String sql = """
            SELECT
                (
                    SELECT COALESCE(SUM(o.total_amount), 0)
                    FROM tbl_order o
                    WHERE o.status = 'COMPLETED'
                      AND YEAR(o.created_at) = YEAR(CURDATE())
                      AND MONTH(o.created_at) = MONTH(CURDATE())
                ) AS revenue,

                (
                    SELECT COUNT(*)
                    FROM tbl_product p
                ) AS total_product,

                (
                    SELECT COUNT(*)
                    FROM tbl_order o
                    WHERE o.status <> 'CANCELLED'
                ) AS total_order,

                (
                    SELECT COUNT(*)
                    FROM tbl_user u
                ) AS total_user
            """;

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            DashboardResponse res = new DashboardResponse();
            res.setRevenue(rs.getBigDecimal("revenue"));
            res.setTotalProduct(rs.getLong("total_product"));
            res.setTotalOrder(rs.getLong("total_order"));
            res.setTotalUser(rs.getLong("total_user"));
            return res;
        });
    }

    public byte[] exportDashboardExcel() throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Dashboard");
            sheet.createFreezePane(0, 3);

            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleFont.setColor(IndexedColors.WHITE.getIndex());

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            Font normalFont = workbook.createFont();
            normalFont.setFontHeightInPoints((short) 11);

            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFont(titleFont);
            titleStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            CellStyle sectionStyle = workbook.createCellStyle();
            sectionStyle.setFont(headerFont);
            sectionStyle.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
            sectionStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            sectionStyle.setAlignment(HorizontalAlignment.CENTER);
            sectionStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            setBorder(headerStyle);

            CellStyle textStyle = workbook.createCellStyle();
            textStyle.setFont(normalFont);
            textStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            setBorder(textStyle);

            CellStyle centerStyle = workbook.createCellStyle();
            centerStyle.cloneStyleFrom(textStyle);
            centerStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle moneyStyle = workbook.createCellStyle();
            moneyStyle.cloneStyleFrom(textStyle);
            moneyStyle.setAlignment(HorizontalAlignment.RIGHT);
            DataFormat dataFormat = workbook.createDataFormat();
            moneyStyle.setDataFormat(dataFormat.getFormat("#,##0"));

            CellStyle numberStyle = workbook.createCellStyle();
            numberStyle.cloneStyleFrom(textStyle);
            numberStyle.setAlignment(HorizontalAlignment.CENTER);
            numberStyle.setDataFormat(dataFormat.getFormat("#,##0"));

            int rowNum = 0;

            Row mainTitle = sheet.createRow(rowNum++);
            mainTitle.setHeightInPoints(30);
            Cell mainTitleCell = mainTitle.createCell(0);
            mainTitleCell.setCellValue("BÁO CÁO THỐNG KÊ DASHBOARD");
            mainTitleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));

            rowNum++;

            Row titleRevenue = sheet.createRow(rowNum++);
            titleRevenue.setHeightInPoints(24);
            Cell revenueTitleCell = titleRevenue.createCell(0);
            revenueTitleCell.setCellValue("DOANH THU THEO THÁNG");
            revenueTitleCell.setCellStyle(sectionStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 2));

            Row revenueHeader = sheet.createRow(rowNum++);
            revenueHeader.setHeightInPoints(22);

            String[] revenueHeaders = {"Năm", "Tháng", "Doanh thu"};
            for (int i = 0; i < revenueHeaders.length; i++) {
                Cell cell = revenueHeader.createCell(i);
                cell.setCellValue(revenueHeaders[i]);
                cell.setCellStyle(headerStyle);
            }

            String revenueSql = """
            SELECT
                YEAR(created_at) AS year,
                MONTH(created_at) AS month,
                COALESCE(SUM(total_amount), 0) AS revenue
            FROM tbl_order
            WHERE status = 'COMPLETED'
            GROUP BY YEAR(created_at), MONTH(created_at)
            ORDER BY year, month
            """;

            List<Map<String, Object>> revenues = jdbcTemplate.queryForList(revenueSql);

            for (Map<String, Object> item : revenues) {
                Row row = sheet.createRow(rowNum++);

                Cell yearCell = row.createCell(0);
                yearCell.setCellValue(((Number) item.get("year")).intValue());
                yearCell.setCellStyle(centerStyle);

                Cell monthCell = row.createCell(1);
                monthCell.setCellValue(((Number) item.get("month")).intValue());
                monthCell.setCellStyle(centerStyle);

                Cell revenueCell = row.createCell(2);
                revenueCell.setCellValue(((Number) item.get("revenue")).doubleValue());
                revenueCell.setCellStyle(moneyStyle);
            }

            rowNum += 2;

            Row titleProduct = sheet.createRow(rowNum++);
            titleProduct.setHeightInPoints(24);
            Cell productTitleCell = titleProduct.createCell(0);
            productTitleCell.setCellValue("TOP 5 SẢN PHẨM BÁN CHẠY");
            productTitleCell.setCellStyle(sectionStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 2));

            Row productHeader = sheet.createRow(rowNum++);
            productHeader.setHeightInPoints(22);

            String[] productHeaders = {"STT", "Tên sản phẩm", "Số lượng bán"};
            for (int i = 0; i < productHeaders.length; i++) {
                Cell cell = productHeader.createCell(i);
                cell.setCellValue(productHeaders[i]);
                cell.setCellStyle(headerStyle);
            }

            String productSql = """
            SELECT
                p.name,
                SUM(oi.quantity) AS total_sold
            FROM tbl_product p
            JOIN tbl_order_item oi ON oi.product_id = p.id
            JOIN tbl_order o ON o.id = oi.order_id
            WHERE o.status = 'COMPLETED'
            GROUP BY p.id, p.name
            ORDER BY total_sold DESC
            LIMIT 5
            """;

            List<Map<String, Object>> products = jdbcTemplate.queryForList(productSql);

            int index = 1;
            for (Map<String, Object> item : products) {
                Row row = sheet.createRow(rowNum++);

                Cell sttCell = row.createCell(0);
                sttCell.setCellValue(index++);
                sttCell.setCellStyle(centerStyle);

                Cell nameCell = row.createCell(1);
                nameCell.setCellValue(String.valueOf(item.get("name")));
                nameCell.setCellStyle(textStyle);

                Cell soldCell = row.createCell(2);
                soldCell.setCellValue(((Number) item.get("total_sold")).longValue());
                soldCell.setCellStyle(numberStyle);
            }

            sheet.setColumnWidth(0, 15 * 256);
            sheet.setColumnWidth(1, 35 * 256);
            sheet.setColumnWidth(2, 20 * 256);

            workbook.write(out);
            return out.toByteArray();
        }
    }

    private void setBorder(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }

    public List<FavouriteResponse> topPick(String gender) {

        String productSql = """
        SELECT
            COUNT(ot.id) AS total_sold,
            p.id AS product_id,
            p.name AS product_name,
            p.price,
            p.slug
        FROM tbl_order o
        JOIN tbl_order_item ot ON ot.order_id = o.id
        JOIN tbl_product p ON p.id = ot.product_id
        JOIN tbl_category c ON c.id = p.category_id
        LEFT JOIN tbl_category cp ON cp.id = c.parent_id
        WHERE o.status = 'COMPLETED'
          AND cp.name = :gender
        GROUP BY p.id, p.name, p.price, p.slug
        ORDER BY total_sold DESC
        LIMIT 8
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("gender", gender);

        List<FavouriteResponse> products = namedParameterJdbcTemplate.query(
                productSql,
                params,
                (rs, rowNum) -> mapProduct(rs)
        );

        if (products.size() < 8) {
            int remain = 8 - products.size();

            List<String> existingIds = products.stream()
                    .map(FavouriteResponse::getProductId)
                    .toList();

            String additionalSql = getString(existingIds);

            MapSqlParameterSource additionalParams = new MapSqlParameterSource()
                    .addValue("gender", gender)
                    .addValue("remain", remain);

            if (!existingIds.isEmpty()) {
                additionalParams.addValue("productIds", existingIds);
            }

            List<FavouriteResponse> additionalProducts =
                    namedParameterJdbcTemplate.query(
                            additionalSql,
                            additionalParams,
                            (rs, rowNum) -> mapProduct(rs)
                    );

            products.addAll(additionalProducts);
        }

        if (products.isEmpty()) {
            return products;
        }

        List<String> productIds = products.stream()
                .map(FavouriteResponse::getProductId)
                .toList();

        String variantSql = """
        SELECT
            pv.product_id,
            pv.color,
            pi.imageurl AS image_url
        FROM tbl_product_variant pv
        LEFT JOIN tbl_product_image pi
            ON pi.variant_id = pv.id
            AND pi.is_main = true
        WHERE pv.product_id IN (:productIds)
        """;

        MapSqlParameterSource variantParams = new MapSqlParameterSource()
                .addValue("productIds", productIds);

        Map<String, List<FavouriteResponse.Variant>> variantMap = new HashMap<>();

        namedParameterJdbcTemplate.query(variantSql, variantParams, rs -> {
            String productId = rs.getString("product_id");

            FavouriteResponse.Variant variant = new FavouriteResponse.Variant(
                    rs.getString("color"),
                    rs.getString("image_url")
            );

            variantMap.computeIfAbsent(productId, k -> new ArrayList<>()).add(variant);
        });

        for (FavouriteResponse product : products) {
            product.setVariants(
                    variantMap.getOrDefault(product.getProductId(), new ArrayList<>())
            );
        }

        return products.stream()
                .limit(8)
                .toList();
    }

    private static @NonNull String getString(List<String> existingIds) {
        String additionalSql = """
        SELECT
            p.id AS product_id,
            p.name AS product_name,
            p.price,
            p.slug
        FROM tbl_product p
        JOIN tbl_category c ON c.id = p.category_id
        LEFT JOIN tbl_category cp ON cp.id = c.parent_id
        WHERE cp.name = :gender
        """;

        if (!existingIds.isEmpty()) {
            additionalSql += " AND p.id NOT IN (:productIds) ";
        }

        additionalSql += " LIMIT :remain ";
        return additionalSql;
    }

    private FavouriteResponse mapProduct(ResultSet rs) throws SQLException {
        FavouriteResponse item = new FavouriteResponse();
        item.setProductId(rs.getString("product_id"));
        item.setProductName(rs.getString("product_name"));
        item.setPrice(rs.getDouble("price"));
        item.setSlug(rs.getString("slug"));
        item.setVariants(new ArrayList<>());
        return item;
    }
}
