package com.example.dao;

import javax.persistence.*;


import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Table(name="kafka_log")
public class KafkaDAO {
    @Id
    @Column(name="log_sequence", nullable=false)
    @GeneratedValue(strategy= GenerationType.SEQUENCE)
    private Integer log_sequence;

    @Column(name="timestamp", nullable=false)
    private String timestamp;

    @Column(name="code", nullable=false)
    private String code;

    @Column(name="guid", nullable=false)
    private String guid;

    @Column(name="msg", nullable=true)
    private String msg;

}
