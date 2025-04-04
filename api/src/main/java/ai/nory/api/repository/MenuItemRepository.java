package ai.nory.api.repository;

import ai.nory.api.entity.MenuItem;
import ai.nory.api.entity.embedded.MenuItemKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, MenuItemKey> {
    List<MenuItem> findByKeyLocationId(Long locationId);
}
