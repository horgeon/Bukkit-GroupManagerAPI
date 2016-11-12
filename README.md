# Group Manager API

Bukkit plugin that expose a HTTP API for Group Manager, using [Prodrivers API](https://github.com/horgeon/ProdriversAPI).

 - Secured with public/private key pairs
 - Possibility to activate/deactivate specific endpoints

Currently exposed endpoints:
 - **GET** /getgroup
   - Receive paramaters:
     - uuid
   - Returns:
     - success (true/false)
	 - group (if possible)
 - **POST** /setgroup
   - Receive paramaters:
     - uuid
     - group
   - Returns:
     - success (true/false)
 - **GET** /reload
   - Returns:
     - success (true/false)