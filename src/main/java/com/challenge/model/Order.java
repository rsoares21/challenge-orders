package com.challenge.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @Column(name = "order_id", nullable = false, unique = true)
    private String orderId;

    @Version
    private Long version; // Usado pelo Hibernate para Optimistic Locking. É incrementado automaticamente a cada atualização.

    /*
     * Mecanismo de Optimistic Locking:
     * - O campo version é verificado durante operações de atualização/exclusão.
     * - O Hibernate inclui a versão na cláusula WHERE para garantir que o registro não foi modificado desde que foi lido.
     * - Se a versão não corresponder (ou seja, outra transação atualizou o registro), a atualização afeta 0 linhas.
     * - O Hibernate detecta isso e lança uma ObjectOptimisticLockingFailureException.
     * - Isso previne atualizações perdidas e garante consistência de dados em ambientes concorrentes.
     */

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
        name = "order_products",
        joinColumns = @JoinColumn(name = "order_id"),
        inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private OrderStatus orderStatus;

    @Column(name = "total_value")
    private double totalValue;
}
