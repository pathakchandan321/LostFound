package com.lostFound.lostFound.repository;
import com.lostFound.lostFound.Entity.ItemMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface ItemMatchRepo extends JpaRepository<ItemMatch, Long> {
    Optional<ItemMatch> findByLostItemIdAndFoundItemId(Long lostId, Long foundId);
    List<ItemMatch> findByLostItemReporterIdOrderByCreatedAtDesc(Long userId);
    List<ItemMatch> findByLostItemIdOrFoundItemId(Long lostId, Long foundId);
}