package cn.com.poka.gzhquery.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import cn.com.poka.gzhquery.dao.inter.BankInfoDao;
import cn.com.poka.gzhquery.domain.BankInfo;
import cn.com.poka.gzhquery.util.JdbcUtil;

public class BankInfoDaoImpl implements BankInfoDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<BankInfo> findBankInfoList() throws SQLException {
		QueryRunner runner = new QueryRunner(JdbcUtil.getDataSource());
		String sql = "SELECT BANKNO,BANKNAME,IPADDR FROM BANKINFO ";
		return (List<BankInfo>)runner.query(sql,null,new BeanListHandler(BankInfo.class));
	}

	@Override
	public List<BankInfo> findBankInfo(String bankNo) throws SQLException {
		QueryRunner runner = new QueryRunner(JdbcUtil.getDataSource());
		String sql = "SELECT BANKNO,BANKNAME,IPADDR FROM BANKINFO WHERE BANKNO=?";
		return (List<BankInfo>)runner.query(sql,bankNo,new BeanListHandler(BankInfo.class));
	}

}
