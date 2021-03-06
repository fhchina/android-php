<?php
/**
 * NAMESPACE Cli
 *
 * @category   NAMESPACE
 * @package    NAMESPACE_Cli
 * @author     James.Huang <huangjuanshi@snda.com>
 * @license    http://www.apache.org/licenses/LICENSE-2.0
 * @version    $Id$
 */

/**
 * @package NAMESPACE_Cli
 */
class NAMESPACE_Cli_Help extends NAMESPACE_Cli
{
	/**
	 * Document cli class instruction
	 * @return void
	 */
	public function helpAction ()
	{
		// Get cli class method list
		$cliMethodList = $this->_getCliMethodList();
		
		// print command list
		echo "\n-------------------------------------------------------\n";
		echo __APP_NAME . " Cli Command List :\n";
		echo "-------------------------------------------------------\n\n";
		foreach ($cliMethodList as $cliName => $cmdList) {
			foreach ($cmdList as $cmdLine => $cmdValue) {
				echo "./cli $cliName $cmdLine\n";
			}
		}
		echo "\n";
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	// protected methods
	
	protected function _getCliMethodList ()
	{
		$cliMethodList = array();
		foreach (glob(__LIB_PATH_CLI . '/*.php') as $classFile) {
			$cliName = strtolower(basename($classFile, '.php'));
			$className = 'NAMESPACE_Cli_' . basename($classFile, '.php');
			if ($classFile && $className) {
				require_once $classFile;
				$rClass = new ReflectionClass($className);
				$methodList = $rClass->getMethods(ReflectionMethod::IS_PUBLIC);
				foreach ($methodList as $method) {
					if (preg_match('/Action$/', $method->name)) {
						$cmdName = str_replace('Action', '', $method->name);
						$cliMethodList[$cliName][$cmdName] = 1;
					}
				}
			}
		}
//		Hush_Util::dump($cliMethodList);exit;
		return $cliMethodList;
	}
}
