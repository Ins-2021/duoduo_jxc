package com.duoduo.jxc.converter;

import com.duoduo.jxc.dto.finance.*;
import com.duoduo.jxc.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import java.util.List;

/**
 * 财务模块转换器
 *
 * @author duoduo
 * @date 2026-03-25
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FinanceConverter {
    FinanceConverter INSTANCE = Mappers.getMapper(FinanceConverter.class);

    // 财务账户
    FinanceAccount toEntity(FinanceAccountDTO dto);
    FinanceAccountDTO toDTO(FinanceAccount entity);
    List<FinanceAccountDTO> toAccountDTOList(List<FinanceAccount> list);

    // 应收账款
    Receivable toEntity(ReceivableDTO dto);
    ReceivableDTO toDTO(Receivable entity);
    List<ReceivableDTO> toReceivableDTOList(List<Receivable> list);

    // 应付账款
    Payable toEntity(PayableDTO dto);
    PayableDTO toDTO(Payable entity);
    List<PayableDTO> toPayableDTOList(List<Payable> list);

    // 收款单
    Receipt toEntity(ReceiptDTO dto);
    ReceiptDTO toDTO(Receipt entity);
    List<ReceiptDTO> toReceiptDTOList(List<Receipt> list);

    // 付款单
    Payment toEntity(PaymentDTO dto);
    PaymentDTO toDTO(Payment entity);
    List<PaymentDTO> toPaymentDTOList(List<Payment> list);

    // 财务流水
    FinanceTransaction toEntity(FinanceTransactionDTO dto);
    FinanceTransactionDTO toDTO(FinanceTransaction entity);
    List<FinanceTransactionDTO> toTransactionDTOList(List<FinanceTransaction> list);

    // 核销单
    WriteOff toEntity(WriteOffDTO dto);
    WriteOffDTO toDTO(WriteOff entity);
    List<WriteOffDTO> toWriteOffDTOList(List<WriteOff> list);

    // 收支单
    IncomeExpense toEntity(IncomeExpenseDTO dto);
    IncomeExpenseDTO toDTO(IncomeExpense entity);
    List<IncomeExpenseDTO> toIncomeExpenseDTOList(List<IncomeExpense> list);
}
