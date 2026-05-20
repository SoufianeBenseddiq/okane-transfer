## Comment les trois s'articulent

`CryptoUtil` fait le vrai travail de chiffrement AES. `CryptoConverter` est le pont entre `CryptoUtil` et Hibernate — il s'applique sur un champ d'entité avec une seule annotation :

```java
// dans Expediteur.java
@Convert(converter = CryptoConverter.class)
@Column(nullable = false)
private String numeroPiece;
// Hibernate chiffre automatiquement avant INSERT
// et déchiffre automatiquement après SELECT
// le service reçoit toujours la valeur en clair
```

`DateUtil` est utilisé dans les services de rapport et de caisse :

```java
// dans RapportServiceImpl.java
LocalDateTime debut = dateUtil.debutJournee(LocalDate.now());
LocalDateTime fin   = dateUtil.finJournee(LocalDate.now());
transfertRepository.sumMontantByAgenceAndPeriode(agence, debut, fin);

// dans CaisseServiceImpl.java
List<CaisseOperation> opsAujourdhui = caisseOperationRepository
    .findByAgentAndDateHeureBetween(
        agent,
        dateUtil.debutJournee(LocalDate.now()),
        dateUtil.finJournee(LocalDate.now())
    );

// dans TransfertServiceImpl — vérifier expiration
if (dateUtil.isExpire(transfert.getCreeLe(), 30)) {
    transfert.setStatut(StatutTransfertEnum.EXPIRE);
}
```