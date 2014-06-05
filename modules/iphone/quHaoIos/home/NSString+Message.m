//
//  NSString+Message.m
//  quHaoIos
//
//  Created by sam on 14-5-29.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import "NSString+Message.h"

@implementation NSString (Message)

- (NSString *)stringByTrimingWhitespace {
    return [self stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
}

- (NSUInteger)numberOfLines {
    return [[self componentsSeparatedByString:@"\n"] count] + 1;
}

@end
