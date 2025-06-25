package com.challenge.service;

import com.challenge.model.Order;
import com.challenge.model.Product;
import com.challenge.model.OrderStatus;
import com.challenge.repository.OrderRepository;
import com.challenge.repository.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderServiceImpl(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Override
    /**
     * Método transacional para salvar um pedido.
     * 
     * Usa o nível de isolamento REPEATABLE_READ para garantir que durante a transação:
     * - Leituras repetidas retornam os mesmos dados (prevenção de non-repeatable reads).
     * - Evita leituras sujas (dirty reads).
     * - Pode permitir phantom reads, mas reduz inconsistências em ambientes concorrentes.
     * 
     * Este nível é um bom equilíbrio entre consistência e desempenho para operações de pedido.
     */
    @Transactional(isolation = org.springframework.transaction.annotation.Isolation.REPEATABLE_READ)
    @CacheEvict(value = "newOrders", allEntries = true)
    public Order saveOrder(Order order) {
        if (order.getProducts() == null || order.getProducts().isEmpty()) {
            throw new RuntimeException("A order deve conter pelo menos um produto.");
        }

        // Fetch products from DB to get current prices
        List<Product> fetchedProducts = productRepository.findAllById(
            order.getProducts().stream().map(Product::getId).toList()
        );

        if (fetchedProducts.size() != order.getProducts().size()) {
            // Find missing product IDs
            List<Long> fetchedIds = fetchedProducts.stream().map(Product::getId).toList();
            for (Product p : order.getProducts()) {
                if (!fetchedIds.contains(p.getId())) {
                    throw new RuntimeException("Produto não encontrado com id: " + p.getId());
                }
            }
        }

        order.setProducts(fetchedProducts);

        double totalValue = calculateTotalValue(order);
        order.setTotalValue(totalValue);
        order.setOrderStatus(OrderStatus.NEW);
        orderRepository.save(order);

        return order;
    }

    @Override
    public double calculateTotalValue(Order order) {
        return order.getProducts().stream()
                .mapToDouble(Product::getPrice)
                .sum();
    }

    @Override
    @Cacheable(value = "newOrders")
    public List<Order> getNewOrders() {
        return orderRepository.findByOrderStatus(OrderStatus.NEW);
    }

}
