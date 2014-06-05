//
//  Emoji.m
//  quHaoIos
//
//  Created by sam on 14-5-29.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import "Emoji.h"
#import "EmojiEmoticons.h"
#import "EmojiMapSymbols.h"
#import "EmojiPictographs.h"
#import "EmojiTransport.h"

@implementation Emoji
+ (NSString *)emojiWithCode:(int)code {
    int sym = EMOJI_CODE_TO_SYMBOL(code);
    return [[NSString alloc] initWithBytes:&sym length:sizeof(sym) encoding:NSUTF8StringEncoding];
}
+ (NSArray *)allEmoji {
    NSMutableArray *array = [NSMutableArray new];
    [array addObjectsFromArray:[EmojiEmoticons allEmoticons]];
    [array addObjectsFromArray:[EmojiMapSymbols allMapSymbols]];
    [array addObjectsFromArray:[EmojiPictographs allPictographs]];
    [array addObjectsFromArray:[EmojiTransport allTransport]];
    
    return array;
}
@end
