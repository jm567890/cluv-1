package com.gsitm.intern.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

    @Entity
    @Table(name = "email_notice")
    @Getter
    @Setter
    @ToString
    public class EmailNotice extends BaseEntity {
        @Id
        @Column(name = "email_notice_id")
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;

        @Lob
        private String email_content;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "order_id")
        private Order order;

    }