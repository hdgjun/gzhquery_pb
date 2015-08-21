package cn.com.poka.gzhquery.controller;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import cn.com.poka.gzhquery.domain.BankInfo;
import cn.com.poka.gzhquery.domain.MoneyDataInfo;
import cn.com.poka.gzhquery.domain.Pagination;
import cn.com.poka.gzhquery.domain.User;
import cn.com.poka.gzhquery.exception.ErrorLoginException;
import cn.com.poka.gzhquery.service.GZHQueryService;
import cn.com.poka.gzhquery.util.CxfUtil;

import com.google.gson.Gson;
import com.poka.app.cb.ws.IgzhQuery;

public class GZHQueryServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	Logger logger = Logger.getLogger(GZHQueryServlet.class);

	public GZHQueryServlet() {
		super();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String method = request.getParameter("method");

		String flag = request.getParameter("login");
		request.setAttribute("login", flag);

		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@" + method);
		// //密钥实体类
		// Arithmetic des = new Arithmetic();
		// // 生成密匙
		// des.getKey("aadd");
		//
		// if(flag.equals("true")||flag.equals("false")){
		// // 加密字符串,返回String的密文
		// String strEnc = des.getEncString(flag);
		// request.setAttribute("login", strEnc);
		//
		// }else{
		//
		// }
		//
		// if(!flag.equals("true")&&!flag.equals("false")){
		// // 把String 类型的密文解密
		// flag= des.getDesString(flag);
		// }

		if (null != flag && flag.trim().equals("true")) {

			if (method != null && method.trim().length() > 0) {
				if ("toIndex".equals(method)) {
					request.getRequestDispatcher("/WEB-INF/jsp/index_.jsp")
							.forward(request, response);
				} else if ("toLoginJsp".equals(method)) {
					request.getRequestDispatcher("/WEB-INF/jsp/login.jsp")
							.forward(request, response);
				} else if ("login".equals(method)) { // login
					this.login(request, response);
				} else if ("query".equals(method)) {
					User user = (User) request.getSession()
							.getAttribute("user");
					if (user != null) {
						request.getRequestDispatcher("/WEB-INF/jsp/query.jsp")
								.forward(request, response);
					} else {
						response.sendRedirect(request.getContextPath()
								+ "/index.jsp");
					}
				} else if ("search".equals(method)) {
					User user = (User) request.getSession()
							.getAttribute("user");
					if (user != null) {
						request.getRequestDispatcher("/WEB-INF/jsp/search.jsp")
								.forward(request, response);
					} else {
						response.sendRedirect(request.getContextPath()
								+ "/index.jsp");
					}
				} else if ("queryResult".equals(method)) {
					System.out.println("*************************" + method);
					queryResult(request, response);
				} else if ("searchResult".equals(method)) {
					searchResult(request, response);
				} else if ("exit".equals(method)) {
					HttpSession session = request.getSession();
					session.invalidate();
					request.getRequestDispatcher("/WEB-INF/jsp/exit.jsp")
							.forward(request, response);
				} else if ("queryBankInfo".equals(method)) {
					queryBankInfo(request, response);
				}
			} else {
				request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(
						request, response);
			}
		} else if (null != flag && flag.trim().equals("false")) {
			if (method != null && method.trim().length() > 0) {
				if ("query".equals(method)) {
					request.getRequestDispatcher("/WEB-INF/jsp/query.jsp")
							.forward(request, response);
				} else if ("search".equals(method)) {
					request.getRequestDispatcher("/WEB-INF/jsp/search.jsp")
							.forward(request, response);
				} else if ("queryResult".equals(method)) {
					queryResult(request, response);
				} else if ("searchResult".equals(method)) {
					searchResult(request, response);
				}
			}

		}

	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doGet(request, response);
	}

	/***
	 * User Login
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void login(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			User user = new User();

			Enumeration<String> enums = request.getParameterNames();
			while (enums.hasMoreElements()) {
				String key = enums.nextElement();
				String[] values = request.getParameterValues(key);
				BeanUtils.setProperty(user, key, values);
			}// end of while loop
			GZHQueryService gzhQueryService = new GZHQueryService();
			user = gzhQueryService.login(user);
			if (user != null) {
				String referer = request.getHeader("referer");
				request.getSession().setAttribute("user", user);
				request.getSession().setAttribute("referer", referer);
				response.setContentType("text/html;charset=UTF-8");
				response.getWriter().write("success");
			} else {
				throw new ErrorLoginException();
			}
		} catch (ErrorLoginException e) {
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().write("errorLogin");
		} catch (Exception e) {
			e.printStackTrace();
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().write("failed");
		}
	}

	/***
	 * query money code information Version 2.0
	 * 
	 * @param request
	 * @param response
	 */
	private void queryResult(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			response.setContentType("text/html;charset=UTF-8");
			String dealNo = request.getParameter("dealNo");
			String bankNo = request.getParameter("bankNo");
			String result = "";
			GZHQueryService gzhQueryService = new GZHQueryService();
			System.out.println("银行号:" + bankNo);
			logger.info("银行号:" + bankNo);
			if ("0".equals(bankNo)) {
				if (dealNo != null && dealNo.trim().length() > 0) {
					dealNo = dealNo.replaceAll(" ", "");
					List<MoneyDataInfo> moneyDataList = null;

					if (dealNo.indexOf("*") > -1) {
						dealNo = dealNo.replace("*", "_");
						moneyDataList = gzhQueryService
								.getMoneyDataListByLike(dealNo);
					} else {
						moneyDataList = gzhQueryService
								.getMoneyDataList(dealNo);
					}
					if (moneyDataList != null && moneyDataList.size() > 0) {
						Gson gson = new Gson();
						String jsonMonList = gson.toJson(moneyDataList);
						response.getWriter().write(jsonMonList);
						return;
					}
				}
			} else {
				BankInfo bank = gzhQueryService.getBankInfo(bankNo).get(0);
				CxfUtil cxfUtil = new CxfUtil();
				if (bank != null) {
					IgzhQuery service = cxfUtil
							.getCxfClient(
									IgzhQuery.class,
									cxfUtil.getUrl(bank.getIpAddr(),
											cxfUtil.getPort()));
					cxfUtil.recieveTimeOutWrapper(service);

					try {
						result = service.gzhQueryList(dealNo);
					} catch (Exception ex) {
						logger.info("连接服务器失败!");
						response.setContentType("text/html;charset=UTF-8");
						response.getWriter().write("notfound");
						return;
					}
					if (!"".equals(result)) {
						response.getWriter().write(result);
						return;
					}
				}
			}
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().write("notfound");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void queryBankInfo(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			response.setContentType("text/html;charset=UTF-8");
			List<BankInfo> bankinfoList = null;
			GZHQueryService gzhQueryService = new GZHQueryService();
			bankinfoList = gzhQueryService.getBankInfoList();
			if (bankinfoList != null && bankinfoList.size() > 0) {
				Gson gson = new Gson();
				String jsonMonList = gson.toJson(bankinfoList);
				response.getWriter().write(jsonMonList);
			} else {
				response.setContentType("text/html;charset=UTF-8");
				response.getWriter().write("notfound");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***
	 * query money code information Version 3.0
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void searchResult(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			request.setCharacterEncoding("UTF-8");
			response.setContentType("text/html;charset=UTF-8");
			String page = request.getParameter("page"); // 当前页数
			String rows = request.getParameter("rows"); // 每页显示行数
			String dealNo = request.getParameter("dealNo");
			String msg = null;
			if (dealNo != null && dealNo.trim().length() > 0 && page != null
					&& page.trim().length() > 0 && rows != null
					&& rows.trim().length() > 0) {
				GZHQueryService gzhQueryService = new GZHQueryService();
				List<MoneyDataInfo> moneyDataList = gzhQueryService
						.getMoneyDataListPage(page, rows, dealNo);
				int total = gzhQueryService.getResultCount(dealNo);
				Pagination pagination = new Pagination();
				// pagination.setRows(moneyDataList);
				pagination.setTotal(total);
				Gson gson = new Gson();
				msg = gson.toJson(pagination);
			} else {
				msg = "notfount";
			}
			response.getWriter().write(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
