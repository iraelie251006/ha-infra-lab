package dev.iralie.inventory.service;

import dev.iralie.inventory.config.RoutingDataSource;
import dev.iralie.inventory.model.Product;
import dev.iralie.inventory.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;

    // ── Reads → replica ──────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<Product> findAll() {
        RoutingDataSource.useReplica();
        try {
            return repository.findAll();
        } finally {
            RoutingDataSource.clear();
        }
    }

    @Transactional(readOnly = true)
    public Product findById(Long id) {
        RoutingDataSource.useReplica();
        try {
            return repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Product not found: " + id));
        } finally {
            RoutingDataSource.clear();
        }
    }

    @Transactional(readOnly = true)
    public List<Product> findByCategory(String category) {
        RoutingDataSource.useReplica();
        try {
            return repository.findByCategory(category);
        } finally {
            RoutingDataSource.clear();
        }
    }

    // ── Writes → primary ─────────────────────────────────────────

    @Transactional
    public Product create(Product product) {
        RoutingDataSource.usePrimary();
        try {
            return repository.save(product);
        } finally {
            RoutingDataSource.clear();
        }
    }

    @Transactional
    public Product update(Long id, Product incoming) {
        RoutingDataSource.usePrimary();
        try {
            Product existing = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Product not found: " + id));
            existing.setName(incoming.getName());
            existing.setCategory(incoming.getCategory());
            existing.setPrice(incoming.getPrice());
            existing.setStockQuantity(incoming.getStockQuantity());
            return repository.save(existing);
        } finally {
            RoutingDataSource.clear();
        }
    }

    @Transactional
    public void delete(Long id) {
        RoutingDataSource.usePrimary();
        try {
            repository.deleteById(id);
        } finally {
            RoutingDataSource.clear();
        }
    }
}