<?php

function random_chars($charNbr)
{
    $chars = array('0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','A','B','C','D','E','F','G', 'H', 'J','K','L', 'M','O', 'P', 'R', 'U', 'S', 'T','W', 'Z');
    $randomchar = '';
    $maxNum = count($chars) -1;
    for($i=0;$i<$charNbr;++$i)
    {
        $randomchar .= $chars[random_int(0,$maxNum)];
    }
    return $randomchar;
}

function kennwort_hash($kennwort) {
    return password_hash($kennwort, PASSWORD_BCRYPT);
}

function kennwort_verify($kennwort, $hash) {
    return password_verify($kennwort, $hash);
}

?>