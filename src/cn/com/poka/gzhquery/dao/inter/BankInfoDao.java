package cn.com.poka.gzhquery.dao.inter;

import java.sql.SQLException;
import java.util.List;

import cn.com.poka.gzhquery.domain.BankInfo;

public interface BankInfoDao {
	public List<BankInfo> findBankInfoList()throws SQLException ;
	public List<BankInfo> findBankInfo(String bankNo)throws SQLException ;
}
