package com.kiemhiep.api.platform;

import java.util.List;

/**
 * Adapter cho world/dimension.
 * Chỉ query entity trong vùng (Rule 4 — không scan cả world).
 */
public interface WorldAdapter {

    /** ID world (vd. dimension registry key string). */
    String getWorldId();

    /**
     * Lấy entity trong hình hộp từ origin mở rộng radius (theo từng trục).
     * Không scan toàn world.
     *
     * @param origin tâm (hoặc góc)
     * @param radius bán kính (block) — box từ origin - radius đến origin + radius
     * @return danh sách entity trong vùng
     */
    List<EntityAdapter> getEntitiesInBox(Location origin, double radius);

    /**
     * Lấy entity trong bán kính từ origin (sphere/box tùy impl).
     * Không scan toàn world.
     *
     * @param origin tâm
     * @param radius bán kính (block)
     * @return danh sách entity trong bán kính
     */
    List<EntityAdapter> getEntitiesInRadius(Location origin, double radius);
}
