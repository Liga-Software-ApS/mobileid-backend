// package dk.liga.mobileid.backendapi.notification.application;

// import java.time.LocalDateTime;
// import java.util.Date;
// import java.util.List;

// import javax.persistence.Basic;
// import javax.persistence.Column;
// import javax.persistence.Entity;
// import javax.persistence.GeneratedValue;
// import javax.persistence.Id;
// import javax.persistence.Temporal;
// import javax.persistence.TemporalType;

// import org.hibernate.annotations.CreationTimestamp;

// import io.swagger.v3.oas.annotations.media.Schema;
// import lombok.Data;
// import lombok.ToString;

// @Entity
// @Schema
// @Data
// @ToString
// class Notification {
//     @Id
// 	@GeneratedValue
//     private long id;
//     String subject;

//     @Column(unique=true)
//     String nonce;
    
//     String origin;
//     String type;
    
//     @Basic(optional = false)
//     @CreationTimestamp
//     private LocalDateTime createdAt;

//     public Notification(String subject, String nonce, String origin, String type) {
//         this.subject = subject;
//         this.nonce = nonce;
//         this.origin = origin;
//         this.type = type;
//     }

//     public Notification() {}
// }