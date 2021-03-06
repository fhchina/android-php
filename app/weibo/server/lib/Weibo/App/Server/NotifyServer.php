<?php
/**
 * Weibo App
 *
 * @category   Weibo
 * @package    Weibo_App_Server
 * @author     James.Huang <huangjuanshi@snda.com>
 * @license    http://www.apache.org/licenses/LICENSE-2.0
 * @version    $Id$
 */

require_once 'Weibo/App/Server.php';

/**
 * @package Weibo_App_Server
 */
class NotifyServer extends Weibo_App_Server
{
	/**
	 * ---------------------------------------------------------------------------------------------
	 * > 全局设置：
	 * <code>
	 * </code>
	 * ---------------------------------------------------------------------------------------------
	 */
	public function __init ()
	{
		parent::__init();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	// service api methods
	
	/**
	 * ---------------------------------------------------------------------------------------------
	 * > 接口说明：获取通知接口
	 * <code>
	 * URL地址：/notify/notice
	 * 提交方式：POST
	 * </code>
	 * ---------------------------------------------------------------------------------------------
	 * @title 获取通知接口
	 * @action /notify/notice
	 * @method get
	 */
	public function noticeAction ()
	{
		$this->doAuth();
		
		// get extra customer info
		$noticeDao = $this->dao->load('Core_Notice');
		$noticeItem = $noticeDao->getByCustomer($this->customer['id']);
		if ($noticeItem) {
			$noticeDao->setRead($this->customer['id']);
			$this->render('10000', 'Get notification ok', array(
				'Notice' => $noticeItem
			));
		}
		$this->render('10012', 'Get notification failed');
	}
}