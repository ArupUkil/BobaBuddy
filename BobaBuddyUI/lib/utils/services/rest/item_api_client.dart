import 'dart:async';
import 'dart:convert' as convert;
import 'package:boba_buddy/core/model/models.dart';
import 'package:http/http.dart' as http;

class ItemApiClient {
  static const url = 'http://10.0.2.2:8080';
  final http.Client _httpClient;

  ItemApiClient({http.Client? httpClient})
      :_httpClient = httpClient ?? http.Client();

  ///Queries database for items contained in a provided store.dart
  Future<List<Item>> getMenu({required String storeId}) async {
    final response = await http.get(Uri.parse(url + '/stores/$storeId/items'));

    if (response.statusCode == 200){
      Iterable menuMap = convert.jsonDecode(response.body);
      return menuMap.map((e) => Item.fromJson(e)).toList();
    }
    throw Exception("Failed to fetch menu from store");
    // todo: Does not check if there are items present. We assume here that all stores contain at least one item. If changed implemented empty list handling needs to be done on full_menu_page as well
  }

  /// Queries the databse for [searchTerm] and returns a list of items sorted by price high to low
  Future<List<Item>> itemSearch(String searchTerm) async {
    final response = await http
        .get(Uri.parse(url + '/items/?name-contain=$searchTerm&sortBy=price'));


    if(response.statusCode == 200) {
      Iterable itemsMap = convert.jsonDecode(response.body);
      return itemsMap.map((e) => Item.fromJson(e)).toList();
    }
    throw Exception("Failed to fetch items");
  }

  /// Queries database and returns the item corresponding to [itemId]
  Future<Item> getItemById(String itemId) async {
    //todo: no error checking in place for invalid ids and no error handling on pages if invalid id is passed
    final response = await http.get(Uri.parse(url + '/items/$itemId'));

    if(response.statusCode == 200) {
      Map<String, dynamic> itemMap = convert.jsonDecode(response.body);
      return Item.fromJson(itemMap);
    }
    throw Exception("Failed to fetch items");
  }

  /// Updates item price corresponding to [itemId] with [newPrice]
  Future<Item> updateItemPrice(String itemId, double newPrice) async {
    //todo: add error handling and return a future rather than void to actually portray success and failure of update
    //todo: also add corresponding handling to pages
    http.Response response = await http.put(
        Uri.parse(url + '/items/$itemId?price=$newPrice'));
    if(response.statusCode != 200){
      throw Exception("Failed to update price");
    }
    return Item.fromJson(convert.jsonDecode(response.body));
  }

  /// Gets 1 item from store.dart corresponding to [storeId]
  Future getOneItemFromStore(String storeId) async {
    http.Response response = await http.get(Uri.parse(url + '/stores/$storeId/items'));
    Iterable menuMap = convert.jsonDecode(response.body);
    List<Item> menu = menuMap.map((e) => Item.fromJson(e)).toList();

    if (menu.isEmpty) return menu;

    return menu[0];
  }

}