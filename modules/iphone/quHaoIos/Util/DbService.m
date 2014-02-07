//
//  DbService.m
//  quHaoApp
//
//  Created by sam on 13-7-28.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import "DbService.h"

@implementation DbService

//通过地址直接得到数据并解析json
+(id)parseData:(NSString *)name
{
    NSString *resourcePath=[[NSBundle mainBundle]resourcePath];

    NSString *path=[resourcePath stringByAppendingPathComponent:name];
    id result;
    NSData *data=[NSData dataWithContentsOfFile:path];
    result=[NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:NULL];
    return result;
}
+(id)getHomeData
{ 

    return [[self parseData:@"test.json"] objectForKey:@"mercharts"] ;
}

+(id)parseJson:(NSData *)data
{
    NSError *error;
    NSArray * result=[NSJSONSerialization JSONObjectWithData:data  options:NSJSONReadingMutableContainers error:&error];
    NSLog(@"%@",error);
    return result;
}

@end
