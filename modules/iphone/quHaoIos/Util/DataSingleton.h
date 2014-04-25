//
//  AppDelegate.h
//  quHaoApp
//
//  Created by sam on 13-7-28.
//  Copyright (c) 2013年 sam. All rights reserved.
//
#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "LoadingCell.h"

@interface DataSingleton : NSObject

//pragma 单例模式

+ (DataSingleton *) Instance;
+ (id)allocWithZone:(NSZone *)zone;

- (UITableViewCell *)getLoadMoreCell:(UITableView *)tableView
                    andLoadingString:(NSString *)loadingString
                        andIsLoading:(BOOL)isLoading;

//返回标示正在加载的选项
- (UITableViewCell *)getLoadMoreCell:(UITableView *)tableView 
                       andIsLoadOver:(BOOL)isLoadOver 
                       andLoadOverString:(NSString *)loadOverString 
                       andLoadingString:(NSString *)loadingString 
                       andIsLoading:(BOOL)isLoading;

@end
