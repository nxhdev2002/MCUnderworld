# Phase 05 — Module Economy

- **Depends on**: Phase 01, 02, 03
- **Tiến độ**: 0% (0/6)
- **Có thể làm song song với**: Phase 04, 06

## Sub-tasks

- [ ] **(1)** Models (Wallet, Transaction, CurrencyType) + interface EconomyService (api)
- [ ] **(2)** WalletRepository + migration (wallets, transactions); prefix bảng kiemhiep_
- [ ] **(3)** EconomyServiceImpl: getBalance, add, subtract, transfer; ghi DB trước, cập nhật cache + publish invalidate
- [ ] **(4)** EconomyModule: onLoad bind EconomyService, onEnable đăng ký listener + command
- [ ] **(5)** Listener (nếu cần) + Commands: xem wallet, pay (player to player), admin add/remove
- [ ] **(6)** Events: TransactionEvent, WalletUpdateEvent — fire khi có giao dịch / cập nhật số dư
- [ ] **(7)** Build + unit test: `./gradlew build`, viết unit test, `./gradlew test`

## Ghi chú

Currency: GOLD, SILVER, SPIRIT_STONE (theo doc). Multi-instance: repository đã cache-aside + MessageBus từ phase 03.
